Groovy Crypto Extensions
========================

[![Build Status](https://travis-ci.org/ataylor284/groovy-crypto-extensions.png?branch=master)](https://travis-ci.org/ataylor284/groovy-crypto-extensions)

The Java API for creating message digests and ciphers for symmetric
encryption are complex difficult to get started with.  This groovy
extension provides simple interfaces that make crypto groovier.

The main goals are:

* A simple interface
* Secure choices for defaults
* Interoperate easily with other crypto libraries


{:toc}


Generating Message Digests
--------------------------

Byte arrays and input streams have been augumented with methods
`md5()`, `sha1()`, and `digest()`.  `digest()` takes the name of a
message digest algorithm as an argument; `md5()` and `sha1()` are
shortcuts for the two most commonly used digests.  To get the MD5
digest of a string:

    "Hello World!".bytes.md5().encodeHex()
    ===> ed076287532e86365e841e92bfc50d8c

The digest is returned as an array of bytes (16 bytes in the case of
MD5).  encodeHex() displays it the familiar readable form.

The digest of an input stream can also be generated:

    // confirm the sha1 of groovy-all artifact from http://search.maven.org/#artifactdetails|org.codehaus.groovy|groovy-all|2.1.5|jar
    def jarLocation = 'http://search.maven.org/remotecontent?filepath=org/codehaus/groovy/groovy-all/2.1.5/groovy-all-2.1.5.jar'
    def sha1 = new URL(jarLocation).openConnection().inputStream.sha1().encodeHex().toString()
    assert sha1 == 'eda9522cc90f16c06dd51739e2d02daafad0b36f'


Generating Message Authentication Codes (MACs)
----------------------------------------------

Byte arrays and input streams have been augumented with methods
`mac()` and , `hmac()`.  Like the digest methods, they take the name
of an algorithm as an argument but they also require a key.  To get a
SHA256 HMAC of a string:

    key = "password".toKey(length: 32)
    hmac = "some plaintext".bytes.hmac(key: key).encodeHex()
    ===> 29e17e11f251d058eed0ac05933be6dafad4afca9d30c5a1783a83185af74937

Keys
----

Method `toKey()` has been added to byte arrays and Strings.  The byte
array version of `toKey()` requires the byte array to be the exact
size of the key.  For example, AES-128 requires a 16 byte (128 bit)
key:

    key = '0123456789012345'.bytes.toKey()

The String version of `toKey` uses
[PBKDF2](https://en.wikipedia.org/wiki/PBKDF2) to generate a key from
a String of any size.  The salt value should be overriden:

    key = 'correct horse battery staple'.toKey(salt: 'TrOub4dor&3'.bytes)


Symmetric Encryption and Decryption
-----------------------------------

Byte arrays and input streams have also been augmented with
`encrypt()` and `decrypt()`.

Both of these methods take a map of parameters that must include at
minimum, a key.  Some examples:

    key = "password".toKey()
    ciphertext = "some plaintext".bytes.encrypt(key: key)
    new String(ciphertext.decrypt(key: key))
    ===> some plaintext

The default is AES/CBC/PKCS5Padding, with a randomly generated
initalization vector.  The initalization vector is prepended to the
output cipher text; to set the initialization vector manually, add
`prependIvToCipherText: true` and `initializationVector: myIV` to the
parameters:

    key = "password".toKey()
    ciphertext = "some plaintext".bytes.encrypt(key: key, initializationVector: [0] * 16, prependIvToCipherText: false) // don't actually use this IV!
    new String(ciphertext.decrypt(key: key, initializationVector: [0] * 16, prependIvToCipherText: false))
    ===> some plaintext

Another example, padding manually using ECB mode:

    key = "password".toKey()
    ciphertext = "some plaintext".padRight(16).bytes.encrypt(key: key, padding: 'NoPadding', mode: 'ECB')
    new String(ciphertext.decrypt(key: key, padding: 'NoPadding', mode: 'ECB')).trim()
    ===> some plaintext

All the algorithms supported by
[JCE](http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher)
are supported:

    key = "password".toKey(algorithm: 'DES', length: 8)
    ciphertext = "some plaintext".bytes.encrypt(key: key, algorithm: 'DES')
    new String(ciphertext.decrypt(key: key, algorithm: 'DES'))
    ===> some plaintext
    
Use InputStream an input stream with an out parameter to encrypt a stream:

    key = "password".toKey()
    out = new FileOutputStream('README.md.encrypted')
    new FileInputStream('README.md').encrypt(key: key, out: out)
