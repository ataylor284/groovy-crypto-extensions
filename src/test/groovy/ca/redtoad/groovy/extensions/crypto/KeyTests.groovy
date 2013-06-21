package ca.redtoad.groovy.extensions.crypto

import javax.crypto.spec.SecretKeySpec
import spock.lang.Specification

public class KeyTests extends Specification {

  def 'create a secret key from bytes'() {
    given:
      def key = "0123456789012345".bytes.toKey()

    expect:
      key instanceof SecretKeySpec
      key.algorithm == 'AES'
      key.encoded.length == 16
  }

  def 'create a secret key from bytes, overriding algorithm'() {
    given:
      def key = "01234567".bytes.toKey(algorithm: 'DES')

    expect:
      key instanceof SecretKeySpec
      key.algorithm == 'DES'
      key.encoded.length == 8
  }

  def 'create secret keys from a password using a salt'() {
    given:
      def password = 'not key sized'
      def key1 = password.toKey(salt: 'salt'.bytes)
      def key2 = password.toKey(salt: 'pepper'.bytes)

    expect:
      key1 instanceof SecretKeySpec
      key2 instanceof SecretKeySpec
      key1.encoded != key2.encoded
  }
}
