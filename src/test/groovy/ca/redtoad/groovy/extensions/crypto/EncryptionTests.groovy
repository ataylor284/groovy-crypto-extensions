package ca.redtoad.groovy.extensions.crypto

import spock.lang.Specification

public class EncryptionTests extends Specification {

    def 'encrypt and decrypt using the default settings'() {
      given:
        def key = '0123456789012345'.bytes.toKey()
        def plaintext = 'plaintext with length exact multiple of block size 0123456789012'
        def ciphertext = plaintext.bytes.encrypt(key: key)

      expect:
        plaintext.bytes.length % 16 == 0
        ciphertext instanceof byte[]
        ciphertext.length == 64
        new String(ciphertext.decrypt(key: key)) == plaintext
    }

    def 'encrypt and decrypt using password based key'() {
      given:
        def key = 'correct horse battery staple'.toKey(salt: 'TrOub4dor&3'.bytes)
        def plaintext = 'plaintext with length exact multiple of block size 0123456789012'
        def ciphertext = plaintext.bytes.encrypt(key: key)

      expect:
        plaintext.bytes.length % 16 == 0
        ciphertext instanceof byte[]
        ciphertext.length == 64
        new String(ciphertext.decrypt(key: key)) == plaintext
    }

    def 'encrypt and decrypt overriding mode and padding with a initializationVector'() {
      given:
        def key = '0123456789012345'.bytes.toKey()
        def iv = [0] * 16
        def plaintext = 'plaintext with length that requires padding'
        def params = [key: key, mode: 'CBC', padding: 'PKCS5Padding', initializationVector: iv]
        def ciphertext = plaintext.bytes.encrypt(params)

      expect:
        plaintext.bytes.length % 16 != 0
        ciphertext instanceof byte[]
        new String(ciphertext.decrypt(params)) == plaintext
    }

    def 'encrypt and decrypt overriding algorithm'() {
      given:
        def key = '01234567'.bytes.toKey(algorithm: 'DES')
        def plaintext = 'plaintext with length exact multiple of block size 01234'
        def ciphertext = plaintext.bytes.encrypt(key: key, algorithm: 'DES')

      expect:
        plaintext.bytes.length % 8 == 0
        ciphertext instanceof byte[]
        new String(ciphertext.decrypt(key: key, algorithm: 'DES')) == plaintext
    }

    def 'encrypt and ensure output matches python implementation'() {
      given:
        def key = '0123456789012345'.bytes.toKey()
        def iv = 'abcdefghijklmnop'.bytes
        def plaintext = 'plaintext with length that requires padding'
        def params = [key: key, mode: 'CFB8', initializationVector: iv]
        def ciphertext = plaintext.bytes.encrypt(params)
        // output of
        // python -c "from Crypto.Cipher import AES; print(AES.new('0123456789012345', AES.MODE_CFB,
        // 'abcdefghijklmnop').encrypt('plaintext with length that requires padding').encode('hex'))"
        def expected = '430a130d25751a28b44b74fed5c33a220e86aae5efa1c4f7611d933b1dd7f555559f3c5cd39c1e1a95af80'

      expect:
        ciphertext.encodeHex().toString() == expected
    }

}
