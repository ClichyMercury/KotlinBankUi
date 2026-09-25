package com.finsim.data.auth

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFRetain
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal object Keychain {

    private const val SERVICE = "com.finsim.auth"

    private val classKey = toKotlinString(kSecClass)
    private val serviceKey = toKotlinString(kSecAttrService)
    private val accountKey = toKotlinString(kSecAttrAccount)
    private val valueDataKey = toKotlinString(kSecValueData)
    private val returnDataKey = toKotlinString(kSecReturnData)
    private val matchLimitKey = toKotlinString(kSecMatchLimit)
    private val accessibleKey = toKotlinString(kSecAttrAccessible)
    private val genericPassword = toKotlinString(kSecClassGenericPassword)
    private val matchLimitOne = toKotlinString(kSecMatchLimitOne)
    private val afterFirstUnlock = toKotlinString(kSecAttrAccessibleAfterFirstUnlock)

    fun read(account: String): String? = memScoped {
        val query = baseQuery(account) + mapOf(
            returnDataKey to true,
            matchLimitKey to matchLimitOne
        )
        val result = alloc<CFTypeRefVar>()
        val status = withQuery(query) { SecItemCopyMatching(it, result.ptr) }
        if (status != errSecSuccess) return@memScoped null
        val data = CFBridgingRelease(result.value) as? NSData ?: return@memScoped null
        NSString.create(data = data, encoding = NSUTF8StringEncoding) as String?
    }

    fun write(account: String, value: String) {
        delete(account)
        val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return
        val attributes = baseQuery(account) + mapOf(
            valueDataKey to data,
            accessibleKey to afterFirstUnlock
        )
        withQuery(attributes) { SecItemAdd(it, null) }
    }

    fun delete(account: String) {
        withQuery(baseQuery(account)) { SecItemDelete(it) }
    }

    private fun baseQuery(account: String): Map<Any?, Any?> = mapOf(
        classKey to genericPassword,
        serviceKey to SERVICE,
        accountKey to account
    )

    @Suppress("UNCHECKED_CAST")
    private inline fun <T> withQuery(query: Map<Any?, Any?>, block: (CFDictionaryRef?) -> T): T {
        val cfQuery = CFBridgingRetain(query) as CFDictionaryRef
        return try {
            block(cfQuery)
        } finally {
            CFRelease(cfQuery)
        }
    }

    private fun toKotlinString(ref: CFStringRef?): String = CFBridgingRelease(CFRetain(ref)) as String
}
