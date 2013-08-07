package ca.redtoad.groovy.extensions.crypto

import groovy.transform.CompileStatic
import java.security.MessageDigest
import javax.crypto.spec.*
import javax.crypto.*

@CompileStatic
class CryptoExtensionMethods {

    static final Map DEFAULT_MAC_PARAMS = [
        algorithm: 'HmacSHA256'
    ]

    static final Map DEFAULT_PBE_PARAMS = [
        salt: 'e51fddac390e'.bytes,
        iterations: 10000,
        length: 16,
        algorithm: 'AES'
    ]

    static final Map DEFAULT_CIPHER_PARAMS = [
        algorithm: 'AES',
        mode: 'CBC',
        padding: 'PKCS5Padding',
        initializationVector: null,
        prependIvToCipherText: true
    ]

    static byte[] digest(byte[] self, String name) {
        MessageDigest digest = MessageDigest.getInstance(name)
        digest.update(self)
        digest.digest()
    }
    
    static byte[] digest(InputStream self, String name) {
        MessageDigest digest = MessageDigest.getInstance(name)
        self.eachByte(8 * 1024) { byte[] buffer, int len -> 
            digest.update(buffer, 0, len)
        }
        digest.digest()
    }
    
    static byte[] md5(byte[] self) {
        digest(self, 'MD5')
    }

    static byte[] md5(InputStream self) {
        digest(self, 'MD5')
    }

    static byte[] sha1(byte[] self) {
        digest(self, 'SHA')
    }

    static byte[] sha1(InputStream self) {
        digest(self, 'SHA')
    }

    static byte[] mac(byte[] self, Map params) {
        mac(new ByteArrayInputStream(self), params)
    }

    static byte[] mac(InputStream self, Map params) {
        Map mergedParams = [:]
        mergedParams.putAll(DEFAULT_MAC_PARAMS)
        mergedParams.putAll(params)
        final mac = Mac.getInstance((String) mergedParams.algorithm)
        mac.init((SecretKeySpec) mergedParams.key)
        self.eachByte(8 * 1024) { byte[] buffer, int len -> 
            mac.update(buffer, 0, len)
        }
        mac.doFinal()
    }

    static byte[] hmac(byte[] self, Map params) {
        hmac(new ByteArrayInputStream(self), params)
    }

    static byte[] hmac(InputStream self, Map params) {
        Map mergedParams = [:]
        mergedParams.putAll(params)
        if ('algorithm' in params) {
            mergedParams.algorithm = "Hmac$params.algorithm"
        }
        mac(self, mergedParams)
    }

    static SecretKeySpec toKey(String self, Map params = [:]) {
        Map mergedParams = [:]
        mergedParams.putAll(DEFAULT_PBE_PARAMS)
        mergedParams.putAll(params)
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        PBEKeySpec pbeKeySpec = new PBEKeySpec(self.toCharArray(), (byte[]) mergedParams.salt,
                                               (int) mergedParams.iterations, ((int) mergedParams.length) * 8)
        new SecretKeySpec(factory.generateSecret(pbeKeySpec).getEncoded(), (String) mergedParams.algorithm)
    }

    static SecretKeySpec toKey(byte[] self, Map params = [:]) {
        Map mergedParams = [:]
        mergedParams.putAll(DEFAULT_PBE_PARAMS)
        mergedParams.putAll(params)
        new SecretKeySpec(self, 0, ((Integer) params.length) ?: self.length, (String) mergedParams.algorithm)
    }

    static void encrypt(InputStream self, Map params) {
        Map mergedParams = [:]
        mergedParams.putAll(DEFAULT_CIPHER_PARAMS)
        mergedParams.putAll(params)
        OutputStream out = (OutputStream) mergedParams.out
        String transformation = [mergedParams.algorithm, mergedParams.mode, mergedParams.padding].join('/')
        byte[] iv = (byte[]) mergedParams.initializationVector
        final cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, (SecretKeySpec) mergedParams.key, iv ? new IvParameterSpec(iv) : null)
        if (mergedParams.prependIvToCipherText && 
            mergedParams.mode != 'ECB') {
            out << cipher.getIV()
        }
        self.eachByte(8 * 1024) { byte[] buffer, int len -> 
            out << cipher.update(buffer, 0, len)
        }
        out << cipher.doFinal()
    }

    static byte[] encrypt(byte[] self, Map params) {
        def out = new ByteArrayOutputStream()
        encrypt(new ByteArrayInputStream(self), [*:params, out: out])
        out.toByteArray()
    }

    static void decrypt(InputStream self, Map params) {
        Map mergedParams = [:]
        mergedParams.putAll(DEFAULT_CIPHER_PARAMS)
        mergedParams.putAll(params)
        OutputStream out = (OutputStream) mergedParams.out
        String transformation = [mergedParams.algorithm, mergedParams.mode, mergedParams.padding].join('/')
        byte[] iv = (byte[]) mergedParams.initializationVector
        final cipher = Cipher.getInstance(transformation)
        if (mergedParams.prependIvToCipherText &&
            mergedParams.mode != 'ECB') {
            iv = new byte[cipher.blockSize]
            self.read(iv)
        }
        cipher.init(Cipher.DECRYPT_MODE, (SecretKeySpec) mergedParams.key, iv ? new IvParameterSpec(iv) : null)
        self.eachByte(8 * 1024) { byte[] buffer, int len -> 
            out << cipher.update(buffer, 0, len)
        }
        out << cipher.doFinal()
    }

    static byte[] decrypt(byte[] self, Map params) {
        def out = new ByteArrayOutputStream()
        decrypt(new ByteArrayInputStream(self), [*:params, out: out])
        out.toByteArray()
    }

}