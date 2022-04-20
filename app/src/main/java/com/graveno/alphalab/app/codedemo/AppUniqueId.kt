package com.graveno.alphalab.app.codedemo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec


object AppUniqueId {

    //region pseudo token
    /**
     * Return pseudo unique ID
     * @return ID
     */
    fun getUniquePsuedoID(): String {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        val m_szDevIDShort =
            "16" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10

        // Thanks to @Roman SL!
        // https://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their device, there will be a duplicate entry
        var serial: String? = null
        try {
            serial = Build::class.java.getField("SERIAL")[null].toString()

            // Go ahead and return the serial for api => 9
            return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        } catch (exception: Exception) {
            // String needs to be initialized
            serial = "serial" // some value
        }

        // Thanks @Joe!
        // https://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }
    //endregion

    //region imei telephony manager
    @SuppressLint("MissingPermission")
    fun getImei(activity: Activity): String {
        val tm =
            activity.baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val tmDevice: String
        val tmSerial: String
        val androidId: String
        tmDevice = "" + tm.deviceId
        tmSerial = "" + tm.simSerialNumber
        androidId =
            "" + Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)

        val deviceUuid = UUID(
            androidId.hashCode().toLong(),
            tmDevice.hashCode().toLong() shl 32 or tmSerial.hashCode()
                .toLong()
        )
        return deviceUuid.toString()
    }
    //endregion


    //region app unique number...
    private val SALT_BYTES: Int = 8
    private const val PBK_ITERATIONS = 1000
    private const val ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val PBE_ALGORITHM = "PBEwithSHA256and128BITAES-CBC-BC"

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeySpecException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidAlgorithmParameterException::class
    )
    fun encrypt(password: String, data: ByteArray): EncryptedData {
        val encData = EncryptedData()
        val rnd = SecureRandom()
        encData.salt = ByteArray(SALT_BYTES)
        encData.iv = ByteArray(16) // AES block size
        rnd.nextBytes(encData.salt)
        rnd.nextBytes(encData.iv)
        val keySpec = PBEKeySpec(password.toCharArray(), encData.salt, PBK_ITERATIONS)
        val secretKeyFactory = SecretKeyFactory.getInstance(PBE_ALGORITHM)
        val key: Key = secretKeyFactory.generateSecret(keySpec)
        val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
        val ivSpec = IvParameterSpec(encData.iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
        encData.encryptedData = cipher.doFinal(data)
        return encData
    }

    @Throws(
        NoSuchAlgorithmException::class,
        InvalidKeySpecException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidAlgorithmParameterException::class
    )
    fun decrypt(
        password: String,
        salt: ByteArray,
        iv: ByteArray,
        encryptedData: ByteArray
    ): ByteArray? {
        val keySpec = PBEKeySpec(password.toCharArray(), salt, PBK_ITERATIONS)
        val secretKeyFactory = SecretKeyFactory.getInstance(PBE_ALGORITHM)
        val key: Key = secretKeyFactory.generateSecret(keySpec)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        return cipher.doFinal(encryptedData)
    }

    class EncryptedData {
        var salt: ByteArray? = null
        var iv: ByteArray? = null
        var encryptedData: ByteArray? = null
    }
    //endregion

    //region hex unique encrypt...
//    /**
//     * The Builder used to create the Encryption instance and that contains the information about
//     * encryption specifications, this instance need to be private and careful managed
//     */
//    private var mBuilder: Builder? = null
//
//    /**
//     * The private and unique constructor, you should use the Encryption.Builder to build your own
//     * instance or get the default proving just the sensible information about encryption
//     */
//    private fun AppUniqueId(builder: Builder) {
//        mBuilder = builder
//    }
//
//    /**
//     * @return an default encryption instance or `null` if occur some Exception, you can
//     * create yur own Encryption instance using the Encryption.Builder
//     */
//    fun getDefault(key: String?, salt: String?, iv: ByteArray?): AppUniqueId? {
//        return try {
//            Builder.getDefaultBuilder(key, salt, iv).build()
//        } catch (e: NoSuchAlgorithmException) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    /**
//     * Encrypt a String
//     *
//     * @param data the String to be encrypted
//     *
//     * @return the encrypted String or `null` if you send the data as `null`
//     *
//     * @throws UnsupportedEncodingException       if the Builder charset name is not supported or if
//     * the Builder charset name is not supported
//     * @throws NoSuchAlgorithmException           if the Builder digest algorithm is not available
//     * or if this has no installed provider that can
//     * provide the requested by the Builder secret key
//     * type or it is `null`, empty or in an invalid
//     * format
//     * @throws NoSuchPaddingException             if no installed provider can provide the padding
//     * scheme in the Builder digest algorithm
//     * @throws InvalidAlgorithmParameterException if the specified parameters are inappropriate for
//     * the cipher
//     * @throws InvalidKeyException                if the specified key can not be used to initialize
//     * the cipher instance
//     * @throws InvalidKeySpecException            if the specified key specification cannot be used
//     * to generate a secret key
//     * @throws BadPaddingException                if the padding of the data does not match the
//     * padding scheme
//     * @throws IllegalBlockSizeException          if the size of the resulting bytes is not a
//     * multiple of the cipher block size
//     * @throws NullPointerException               if the Builder digest algorithm is `null` or
//     * if the specified Builder secret key type is
//     * `null`
//     * @throws IllegalStateException              if the cipher instance is not initialized for
//     * encryption or decryption
//     */
//    @Throws(
//        UnsupportedEncodingException::class,
//        NoSuchAlgorithmException::class,
//        NoSuchPaddingException::class,
//        InvalidAlgorithmParameterException::class,
//        InvalidKeyException::class,
//        InvalidKeySpecException::class,
//        BadPaddingException::class,
//        IllegalBlockSizeException::class
//    )
//    fun encrypt(data: String?): String? {
//        if (data == null) return null
//        val secretKey = getSecretKey(hashTheKey(mBuilder.getKey()))
//        val dataBytes = data.toByteArray(charset(mBuilder.getCharsetName()))
//        val cipher = Cipher.getInstance(mBuilder.getAlgorithm())
//        cipher.init(
//            Cipher.ENCRYPT_MODE,
//            secretKey,
//            mBuilder.getIvParameterSpec(),
//            mBuilder.getSecureRandom()
//        )
//        return Base64.encodeToString(cipher.doFinal(dataBytes), mBuilder.getBase64Mode())
//    }
//
//    /**
//     * This is a sugar method that calls encrypt method and catch the exceptions returning
//     * `null` when it occurs and logging the error
//     *
//     * @param data the String to be encrypted
//     *
//     * @return the encrypted String or `null` if you send the data as `null`
//     */
//    fun encryptOrNull(data: String?): String? {
//        return try {
//            encrypt(data)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    /**
//     * This is a sugar method that calls encrypt method in background, it is a good idea to use this
//     * one instead the default method because encryption can take several time and with this method
//     * the process occurs in a AsyncTask, other advantage is the Callback with separated methods,
//     * one for success and other for the exception
//     *
//     * @param data     the String to be encrypted
//     * @param callback the Callback to handle the results
//     */
//    fun encryptAsync(data: String?, callback: Callback?) {
//        if (callback == null) return
//        Thread {
//            try {
//                val encrypt = encrypt(data)
//                if (encrypt == null) {
//                    callback.onError(Exception("Encrypt return null, it normally occurs when you send a null data"))
//                }
//                callback.onSuccess(encrypt)
//            } catch (e: Exception) {
//                callback.onError(e)
//            }
//        }.start()
//    }
//
//    /**
//     * Decrypt a String
//     *
//     * @param data the String to be decrypted
//     *
//     * @return the decrypted String or `null` if you send the data as `null`
//     *
//     * @throws UnsupportedEncodingException       if the Builder charset name is not supported or if
//     * the Builder charset name is not supported
//     * @throws NoSuchAlgorithmException           if the Builder digest algorithm is not available
//     * or if this has no installed provider that can
//     * provide the requested by the Builder secret key
//     * type or it is `null`, empty or in an invalid
//     * format
//     * @throws NoSuchPaddingException             if no installed provider can provide the padding
//     * scheme in the Builder digest algorithm
//     * @throws InvalidAlgorithmParameterException if the specified parameters are inappropriate for
//     * the cipher
//     * @throws InvalidKeyException                if the specified key can not be used to initialize
//     * the cipher instance
//     * @throws InvalidKeySpecException            if the specified key specification cannot be used
//     * to generate a secret key
//     * @throws BadPaddingException                if the padding of the data does not match the
//     * padding scheme
//     * @throws IllegalBlockSizeException          if the size of the resulting bytes is not a
//     * multiple of the cipher block size
//     * @throws NullPointerException               if the Builder digest algorithm is `null` or
//     * if the specified Builder secret key type is
//     * `null`
//     * @throws IllegalStateException              if the cipher instance is not initialized for
//     * encryption or decryption
//     */
//    @Throws(
//        UnsupportedEncodingException::class,
//        NoSuchAlgorithmException::class,
//        InvalidKeySpecException::class,
//        NoSuchPaddingException::class,
//        InvalidAlgorithmParameterException::class,
//        InvalidKeyException::class,
//        BadPaddingException::class,
//        IllegalBlockSizeException::class
//    )
//    fun decrypt(data: String?): String? {
//        if (data == null) return null
//        val dataBytes: ByteArray = Base64.decode(data, mBuilder.getBase64Mode())
//        val secretKey = getSecretKey(hashTheKey(mBuilder.getKey()))
//        val cipher = Cipher.getInstance(mBuilder.getAlgorithm())
//        cipher.init(
//            Cipher.DECRYPT_MODE,
//            secretKey,
//            mBuilder.getIvParameterSpec(),
//            mBuilder.getSecureRandom()
//        )
//        val dataBytesDecrypted = cipher.doFinal(dataBytes)
//        return String(dataBytesDecrypted)
//    }
//
//    /**
//     * This is a sugar method that calls decrypt method and catch the exceptions returning
//     * `null` when it occurs and logging the error
//     *
//     * @param data the String to be decrypted
//     *
//     * @return the decrypted String or `null` if you send the data as `null`
//     */
//    fun decryptOrNull(data: String?): String? {
//        return try {
//            decrypt(data)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    /**
//     * This is a sugar method that calls decrypt method in background, it is a good idea to use this
//     * one instead the default method because decryption can take several time and with this method
//     * the process occurs in a AsyncTask, other advantage is the Callback with separated methods,
//     * one for success and other for the exception
//     *
//     * @param data     the String to be decrypted
//     * @param callback the Callback to handle the results
//     */
//    fun decryptAsync(data: String?, callback: Callback?) {
//        if (callback == null) return
//        Thread {
//            try {
//                val decrypt = decrypt(data)
//                if (decrypt == null) {
//                    callback.onError(Exception("Decrypt return null, it normally occurs when you send a null data"))
//                }
//                callback.onSuccess(decrypt)
//            } catch (e: Exception) {
//                callback.onError(e)
//            }
//        }.start()
//    }
//
//    /**
//     * creates a 128bit salted aes key
//     *
//     * @param key encoded input key
//     *
//     * @return aes 128 bit salted key
//     *
//     * @throws NoSuchAlgorithmException     if no installed provider that can provide the requested
//     * by the Builder secret key type
//     * @throws UnsupportedEncodingException if the Builder charset name is not supported
//     * @throws InvalidKeySpecException      if the specified key specification cannot be used to
//     * generate a secret key
//     * @throws NullPointerException         if the specified Builder secret key type is `null`
//     */
//    @Throws(
//        NoSuchAlgorithmException::class,
//        UnsupportedEncodingException::class,
//        InvalidKeySpecException::class
//    )
//    private fun getSecretKey(key: CharArray): SecretKey {
//        val factory = SecretKeyFactory.getInstance(mBuilder.getSecretKeyType())
//        val spec: KeySpec = PBEKeySpec(
//            key,
//            mBuilder.getSalt().toByteArray(charset(mBuilder.getCharsetName())),
//            mBuilder.getIterationCount(),
//            mBuilder.getKeyLength()
//        )
//        val tmp = factory.generateSecret(spec)
//        return SecretKeySpec(tmp.encoded, mBuilder.getKeyAlgorithm())
//    }
//
//    /**
//     * takes in a simple string and performs an sha1 hash
//     * that is 128 bits long...we then base64 encode it
//     * and return the char array
//     *
//     * @param key simple inputted string
//     *
//     * @return sha1 base64 encoded representation
//     *
//     * @throws UnsupportedEncodingException if the Builder charset name is not supported
//     * @throws NoSuchAlgorithmException     if the Builder digest algorithm is not available
//     * @throws NullPointerException         if the Builder digest algorithm is `null`
//     */
//    @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class)
//    private fun hashTheKey(key: String?): CharArray {
//        val messageDigest: MessageDigest = MessageDigest.getInstance(mBuilder.getDigestAlgorithm())
//        messageDigest.update(key!!.toByteArray(charset(mBuilder.getCharsetName())))
//        return Base64.encodeToString(messageDigest.digest(), Base64.NO_PADDING).toCharArray()
//    }
//
//    /**
//     * When you encrypt or decrypt in callback mode you get noticed of result using this interface
//     */
//    interface Callback {
//        /**
//         * Called when encrypt or decrypt job ends and the process was a success
//         *
//         * @param result the encrypted or decrypted String
//         */
//        fun onSuccess(result: String?)
//
//        /**
//         * Called when encrypt or decrypt job ends and has occurred an error in the process
//         *
//         * @param exception the Exception related to the error
//         */
//        fun onError(exception: Exception?)
//    }
//
//    /**
//     * This class is used to create an Encryption instance, you should provide ALL data or start
//     * with the Default Builder provided by the getDefaultBuilder method
//     */
//    class Builder {
//        /**
//         * @return the IvParameterSpec bytes array
//         */
//        private var iv: ByteArray? = null
//
//        /**
//         * @return the length of key
//         */
//        private var keyLength = 0
//
//        /**
//         * @return the Base 64 mode
//         */
//        private var base64Mode = 0
//
//        /**
//         * @return the number of times the password is hashed
//         */
//        private var iterationCount = 0
//
//        /**
//         * @return the value used for salting
//         */
//        private var salt: String? = null
//
//        /**
//         * @return the key
//         */
//        private var key: String? = null
//
//        /**
//         * @return the algorithm
//         */
//        private var algorithm: String? = null
//
//        /**
//         * @return the key algorithm
//         */
//        private var keyAlgorithm: String? = null
//
//        /**
//         * @return the charset name
//         */
//        private var charsetName: String? = null
//
//        /**
//         * @return the type of aes key that will be created, on KITKAT+ the API has changed, if you
//         * are getting problems please @see [http://android-developers.blogspot.com.br/2013/12/changes-to-secretkeyfactory-api-in.html](http://android-developers.blogspot.com.br/2013/12/changes-to-secretkeyfactory-api-in.html)
//         */
//        private var secretKeyType: String? = null
//
//        /**
//         * @return the message digest algorithm
//         */
//        private var digestAlgorithm: String? = null
//
//        /**
//         * @return the algorithm used to generate the secure random
//         */
//        private var secureRandomAlgorithm: String? = null
//
//        /**
//         * @return the SecureRandom
//         */
//        private var secureRandom: SecureRandom? = null
//        private var mIvParameterSpec: IvParameterSpec? = null
//
//        /**
//         * Build the Encryption with the provided information
//         *
//         * @return a new Encryption instance with provided information
//         *
//         * @throws NoSuchAlgorithmException if the specified SecureRandomAlgorithm is not available
//         * @throws NullPointerException     if the SecureRandomAlgorithm is `null` or if the
//         * IV byte array is null
//         */
//        @Throws(NoSuchAlgorithmException::class)
//        fun build(): AppUniqueId {
//            setSecureRandom(SecureRandom.getInstance(secureRandomAlgorithm))
//            setIvParameterSpec(IvParameterSpec(iv))
//            return AppUniqueId(this)
//        }
//
//        /**
//         * @param charsetName the new charset name
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setCharsetName(charsetName: String?): Builder {
//            this.charsetName = charsetName
//            return this
//        }
//
//        /**
//         * @param algorithm the algorithm to be used
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setAlgorithm(algorithm: String?): Builder {
//            this.algorithm = algorithm
//            return this
//        }
//
//        /**
//         * @param keyAlgorithm the keyAlgorithm to be used in keys
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setKeyAlgorithm(keyAlgorithm: String?): Builder {
//            this.keyAlgorithm = keyAlgorithm
//            return this
//        }
//
//        /**
//         * @param base64Mode set the base 64 mode
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setBase64Mode(base64Mode: Int): Builder {
//            this.base64Mode = base64Mode
//            return this
//        }
//
//        /**
//         * @param secretKeyType the type of AES key that will be created, on KITKAT+ the API has
//         * changed, if you are getting problems please @see [http://android-developers.blogspot.com.br/2013/12/changes-to-secretkeyfactory-api-in.html](http://android-developers.blogspot.com.br/2013/12/changes-to-secretkeyfactory-api-in.html)
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setSecretKeyType(secretKeyType: String?): Builder {
//            this.secretKeyType = secretKeyType
//            return this
//        }
//
//        /**
//         * @param salt the value used for salting
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setSalt(salt: String?): Builder {
//            this.salt = salt
//            return this
//        }
//
//        /**
//         * @param key the key.
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setKey(key: String?): Builder {
//            this.key = key
//            return this
//        }
//
//        /**
//         * @param keyLength the length of key
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setKeyLength(keyLength: Int): Builder {
//            this.keyLength = keyLength
//            return this
//        }
//
//        /**
//         * @param iterationCount the number of times the password is hashed
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setIterationCount(iterationCount: Int): Builder {
//            this.iterationCount = iterationCount
//            return this
//        }
//
//        /**
//         * @param secureRandomAlgorithm the algorithm to generate the secure random
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setSecureRandomAlgorithm(secureRandomAlgorithm: String?): Builder {
//            this.secureRandomAlgorithm = secureRandomAlgorithm
//            return this
//        }
//
//        /**
//         * @param iv the byte array to create a new IvParameterSpec
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setIv(iv: ByteArray?): Builder {
//            this.iv = iv
//            return this
//        }
//
//        /**
//         * @param secureRandom the Secure Random
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setSecureRandom(secureRandom: SecureRandom?): Builder {
//            this.secureRandom = secureRandom
//            return this
//        }
//
//        /**
//         * @return the IvParameterSpec
//         */
//        private val ivParameterSpec: IvParameterSpec?
//            private get() = mIvParameterSpec
//
//        /**
//         * @param ivParameterSpec the IvParameterSpec
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setIvParameterSpec(ivParameterSpec: IvParameterSpec?): Builder {
//            mIvParameterSpec = ivParameterSpec
//            return this
//        }
//
//        /**
//         * @param digestAlgorithm the algorithm to be used to get message digest instance
//         *
//         * @return this instance to follow the Builder patter
//         */
//        fun setDigestAlgorithm(digestAlgorithm: String?): Builder {
//            this.digestAlgorithm = digestAlgorithm
//            return this
//        }
//
//        companion object {
//            /**
//             * @return an default builder with the follow defaults:
//             * the default char set is UTF-8
//             * the default base mode is Base64
//             * the Secret Key Type is the PBKDF2WithHmacSHA1
//             * the default salt is "some_salt" but can be anything
//             * the default length of key is 128
//             * the default iteration count is 65536
//             * the default algorithm is AES in CBC mode and PKCS 5 Padding
//             * the default secure random algorithm is SHA1PRNG
//             * the default message digest algorithm SHA1
//             */
//            fun getDefaultBuilder(key: String?, salt: String?, iv: ByteArray?): Builder {
//                return Builder()
//                    .setIv(iv)
//                    .setKey(key)
//                    .setSalt(salt)
//                    .setKeyLength(128)
//                    .setKeyAlgorithm("AES")
//                    .setCharsetName("UTF8")
//                    .setIterationCount(1)
//                    .setDigestAlgorithm("SHA1")
//                    .setBase64Mode(Base64.DEFAULT)
//                    .setAlgorithm("AES/CBC/PKCS5Padding")
//                    .setSecureRandomAlgorithm("SHA1PRNG")
//                    .setSecretKeyType("PBKDF2WithHmacSHA1")
//            }
//        }
//    }
    //endregion

    //region unique encrypt...
//    fun toEncrypt(
//        messageToEncrypt : String,
//        secretKey : SecretKey
//    ) {
//        val secret : SecretKey = generateKey()
//        encryptMsg(message = messageToEncrypt, secret = secretKey))
//    }
//
//    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
//    fun generateKey(): SecretKey {
//        return SecretKeySpec(password.getBytes(), "AES").also { secret = it }
//    }
//
//    @Throws(
//        NoSuchAlgorithmException::class,
//        NoSuchPaddingException::class,
//        InvalidKeyException::class,
//        InvalidParameterSpecException::class,
//        IllegalBlockSizeException::class,
//        BadPaddingException::class,
//        UnsupportedEncodingException::class
//    )
//    fun encryptMsg(message: String, secret: SecretKey?): ByteArray? {
//        /* Encrypt the message. */
//        var cipher: Cipher? = null
//        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
//        cipher.init(Cipher.ENCRYPT_MODE, secret)
//        return cipher.doFinal(message.toByteArray(charset("UTF-8")))
//    }
//
//    @Throws(
//        NoSuchPaddingException::class,
//        NoSuchAlgorithmException::class,
//        InvalidParameterSpecException::class,
//        InvalidAlgorithmParameterException::class,
//        InvalidKeyException::class,
//        BadPaddingException::class,
//        IllegalBlockSizeException::class,
//        UnsupportedEncodingException::class
//    )
//    fun decryptMsg(cipherText: ByteArray?, secret: SecretKey?): String? {
//        /* Decrypt the message, given derived encContentValues and initialization vector. */
//        var cipher: Cipher? = null
//        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
//        cipher.init(Cipher.DECRYPT_MODE, secret)
//        return String(cipher.doFinal(cipherText), "UTF-8")
//    }
    //endregion
}