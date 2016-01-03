package pmd.di.ubi.pt.testinglogin.activity;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Base64;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Created by jo on 12/31/15.
 */
public class TokenOperation {
    private String name;
    private String ident;
    private String pvk;
    private String pubk;
    private String sign;
    private Context context;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getPvk() {
        return pvk;
    }

    public void setPvk(String pvk) {
        this.pvk = pvk;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


    public TokenOperation(String name, Context context){
        this.name=name;
        this.context=context;
       // this.pvk=pvk;

       //SavekeysTofile("");

        test(context);

    }


    //Gerar uma string aleatoria

    private void test(Context c){
        //create key pair

        KeyPair kp= createKeyPair();
        String pri = Base64.encodeToString(kp.getPrivate().getEncoded(), Base64.DEFAULT);
        String pub=Base64.encodeToString(kp.getPublic().getEncoded(), Base64.DEFAULT);

        //save key pair
        SavekeysTofile("pv.key",pri,c);
        SavekeysTofile("pub.key",pub,c);

        //load key pair from file

       pvk= LoadFiles("pv.key", c);
       pubk= LoadFiles("pub.key", c);

        //sign file
        setIdent(genarateSecToken());
        setSign(getDigitalSignature(name + ident, pvk));


        //verify sign
        verfiySignature(sign,name+ident,pubk);
    }


    public static String genarateSecToken(){
        //  SecretKey secretKey;
        String secret=null;

        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();

        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        keyGenerator.init(outputKeyLength, secureRandom);
        SecretKey key = keyGenerator.generateKey();
        if (key != null) {secret = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);}
        return secret;
    }




    //Create rsa keyPair

    public static KeyPair createKeyPair() {
        KeyPair keyPair = null;

        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            keygen.initialize(1024);
            keyPair = keygen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return keyPair;
    }


    //Send Keypair to data base

    public static boolean Sendkeytodb(KeyPair key){

        //encode pub and private to string
        String pri = Base64.encodeToString(key.getPrivate().getEncoded(), Base64.DEFAULT);
        String pub = Base64.encodeToString(key.getPublic().getEncoded(), Base64.DEFAULT);

        return true;
    }


    public static String getDigitalSignature(String text, String strPrivateKey)  {

        try {

            // Get private key from String
            PrivateKey pk = loadPrivateKey(strPrivateKey);

            // text to bytes
            byte[] data = text.getBytes("UTF8");

            // signature
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(pk);

            sig.update(data);
            byte[] signatureBytes = sig.sign();

            return Base64.encodeToString(signatureBytes,Base64.DEFAULT);

        }catch(Exception e){
            return null;
        }

    }

    public static boolean verfiySignature(String signature, String original, String publicKey){

        try{

            // Get private key from String
            PublicKey pk = loadPublicKey(publicKey);

            // text to bytes
            byte[] originalBytes = original.getBytes("UTF8");


            byte[] signatureBytes =Base64.decode(signature, Base64.DEFAULT);

            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(pk);
            sig.update(originalBytes);
            Log.i("TAG", "correct signature");
            return sig.verify(signatureBytes);

        }catch(Exception e){
            e.printStackTrace();
            Log.i("TAG", "sign eRRor");
           /* Logger log = Logger.getLogger(RsaCipher.class);
            log.error("error for signature:" + e.getMessage());*/
            return false;
        }

    }

    public static PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
        byte[] clear = new byte[0];
        try {
            clear = Base64.decode(key64.getBytes("utf-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    public static PublicKey loadPublicKey(String key64) throws GeneralSecurityException {
        byte[] data = new byte[0];
        try {
            data = Base64.decode(key64.getBytes("utf-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
    }


    public static void SavekeysTofile(String filename, String key, Context context ){
        FileOutputStream outputStream;


        try {
            outputStream = context.openFileOutput(filename,0);
            outputStream.write(key.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("TAG", "fille add");

    }

    public static String LoadFiles(String fileName, Context t ){
        byte [ ] baBuffer = new byte[0];
        FileInputStream fis = null;
        try {
            fis = t.openFileInput(fileName);

            InputStreamReader isr = new InputStreamReader(fis);
            baBuffer = new byte [ fis.available()] ;
            fis.read(baBuffer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String str = new String(baBuffer);
        Log.i("TAG",str);
        return str;
    }

}
