package ca.redtoad.groovy.extensions.crypto

import spock.lang.Specification

public class MacTests extends Specification {

    def 'test known sha256 hmac of a string'() {
      given:
        def key = '01234567890123456789012345678901'.bytes.toKey()
        def message = "Hello World!\n".bytes

      expect:
        message.mac(key: key).encodeHex().toString() == 
            'd1a7e5cae16897cafa780434c888ba1cc2ff7f719164fdcd5c4cd623626492e4'
    }

    def 'test known sha256 hmac of an input stream'() {
      given:
        def key = '01234567890123456789012345678901'.bytes.toKey()
        // mersenne prime 28
        def stream = new ByteArrayInputStream((2 ** 86243 - 1).toByteArray())

      expect:
        stream.mac(key: key).encodeHex().toString() == 
            '1145f52ac032d7ba6129fe4ce26ed3e7a44ba228ff0cde09fb47542b124c2a1f'
    }

    def 'test known md5 hmac of a string'() {
      given:
        def key = '01234567'.bytes.toKey()
        def message = "Hello World!\n".bytes

      expect:
        message.hmac(key: key, algorithm: 'MD5').encodeHex().toString() == 'a4b6172bd0082d605fb877d9f85b8f5a'
    }

    def 'test known md5 hmac of an input stream'() {
      given:
        def key = '01234567'.bytes.toKey()
        // mersenne prime 28
        def stream = new ByteArrayInputStream((2 ** 86243 - 1).toByteArray())

      expect:
        stream.hmac(key: key, algorithm: 'MD5').encodeHex().toString() == '2d0f63adc1a025987387e0b2889dfc80'
    }

}
