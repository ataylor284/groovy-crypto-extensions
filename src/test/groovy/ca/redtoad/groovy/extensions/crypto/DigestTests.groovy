package ca.redtoad.groovy.extensions.crypto

import spock.lang.Specification

public class DigestTests extends Specification {

    def 'test known md5 digest of a string'() {
      given:
        def message = "Hello World!\n".bytes

      expect:
        message.md5().encodeHex().toString() == '8ddd8be4b179a529afa5f2ffae4b9858'
    }

    def 'test known md5 digest of an input stream'() {
      given:
        // mersenne prime 28
        def stream = new ByteArrayInputStream((2 ** 86243 - 1).toByteArray())

      expect:
        stream.md5().encodeHex().toString() == 'e9ffaa566c5407b90f50cfe22c34afd3'
    }

    def 'test known sha1 digest of a string'() {
      given:
        def message = "Hello World!\n".bytes

      expect:
        message.sha1().encodeHex().toString() == 'a0b65939670bc2c010f4d5d6a0b3e4e4590fb92b'
    }

    def 'test known sha1 digest of an input stream'() {
      given:
        // mersenne prime 28
        def stream = new ByteArrayInputStream((2 ** 86243 - 1).toByteArray())

      expect:
        stream.sha1().encodeHex().toString() == '8b21acb1e8c3faa9ef6f605d9c33b1f01d3e4a61'
    }

    def 'test known sha512 digest of a string'() {
      given:
        def message = "Hello World!\n".bytes

      expect:
        message.digest('SHA-512').encodeHex().toString() == 
            '830445e86a0cfafac4e1531002356f384847a11a7456fb8ccb81ab36e37bff28f34fa2c5bfdd347e964c5c5df0fc305de6394368219307b2ceeb0ec84b7c2b31'
    }

    def 'test known sha512 digest of an input stream'() {
      given:
        // mersenne prime 28
        def stream = new ByteArrayInputStream((2 ** 86243 - 1).toByteArray())

      expect:
        stream.digest('SHA-512').encodeHex().toString() == 
            '87ca6ec62a5ae190370c5345d5e02c4751ba3e0baad545b39a9b76e44015bd9f2ab5d58c054215ac52ea873bd9daa6e2d47f3f038c426f1ebd7c74095761d466'
    }

}
