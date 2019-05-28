package com.example.chetan.firebasechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.CorrectionInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class Chat extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    static EditText messageArea;
    EditText password;
    ScrollView scrollView;
    TextView userText;
    Firebase reference1, reference2,reference3,reference4;
    AlertDialog.Builder builder;
    AlertDialog alert;
    Button buttonOk,buttonCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userText = (TextView)findViewById(R.id.userText);
        userText.setText(UserDetails.chatWith);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://fir-chat-b3b0a.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://fir-chat-b3b0a.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);
        reference3 = new Firebase("https://fir-chat-b3b0a.firebaseio.com/messages/" + UserDetails.username + "_Encrypted_" + UserDetails.chatWith);
        reference4 = new Firebase("https://fir-chat-b3b0a.firebaseio.com/messages/" + UserDetails.chatWith + "_Encrypted_" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                /*String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
                String message = messageText;
                message = message.toLowerCase();
                int key = 5;
                String text = "";
                for (int i = 0; i < message.length(); i++)
                {
                    int charPosition = ALPHABET.indexOf(message.charAt(i));
                    int keyVal = (key + charPosition) % 26;
                    char replace = ALPHABET.charAt(keyVal);
                    text += replace;
                }
                //return text;
                String encrymessageText = text;*/

                //String plainText = "Hello World";
                SecretKey secKey = null;
                try {
                    secKey = getSecretEncryptionKey();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byte[] cipherText = new byte[0];
                try {
                    cipherText = encryptText(messageText, secKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String decryptedText = decryptText(cipherText, secKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("Original Text:" + plainText);
                //System.out.println("AES Key (Hex Form):"+bytesToHex(secKey.getEncoded()));
                //System.out.println("Encrypted Text (Hex Form):"+bytesToHex(cipherText));
                //System.out.println("Descrypted Text:"+decryptedText);

                String encrymessageText = cipherText.toString();

                if (!messageText.equals("")) {
                    Map<String, String> map = new HashMap<String, String>();
                    Map<String, String> encrymap = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    encrymap.put("message", encrymessageText);
                    encrymap.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    reference3.push().setValue(encrymap);
                    reference4.push().setValue(encrymap);
                    messageArea.setText("");
                }
            }
        });


            reference1.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Map map = dataSnapshot.getValue(Map.class);
                    String message = map.get("message").toString();
                    String userName = map.get("user").toString();

                    if (userName.equals(UserDetails.username)) {
                        addMessageBox("You:-\n" + message, 1);
                    } else {
                        addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case R.id.Encrypt :
                reference1.removeValue();
                reference3.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Map map = dataSnapshot.getValue(Map.class);
                        String message = map.get("message").toString();
                        String userName = map.get("user").toString();

                        if (userName.equals(UserDetails.username)) {
                            addMessageBox("You:-\n" + message, 1);
                        } else {
                            addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
                        }
                        layout.removeAllViews();
                        reference1.push().setValue(map);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                Toast.makeText(this,"Encrypted",Toast.LENGTH_LONG).show();
                break;
            case R.id.Decrypt :
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Password");
                builder.setIcon(R.drawable.ic_key);

                View v = getLayoutInflater().inflate(R.layout.dialog_ask_password, null);
                builder.setView(v);

                buttonOk = (Button)v.findViewById(R.id.buttonOk);
                buttonCancel = (Button)v.findViewById(R.id.buttonCancel);
                password  = (EditText)v.findViewById(R.id.editTextPassword);

                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pass1= password.getText().toString();

                        //checks hash value
                        String passwordToHash = pass1;
                        String generatedPassword = null;
                        try {
                            // Create MessageDigest instance for MD5
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            //Add password bytes to digest
                            md.update(passwordToHash.getBytes());
                            //Get the hash's bytes
                            byte[] bytes = md.digest();
                            //This bytes[] has bytes in decimal format;
                            //Convert it to hexadecimal format
                            StringBuilder sb = new StringBuilder();
                            for(int i=0; i< bytes.length ;i++)
                            {
                                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                            }
                            //Get complete hashed password in hex format
                            generatedPassword = sb.toString();
                        }
                        catch (NoSuchAlgorithmException e)
                        {
                            e.printStackTrace();
                        }
                        pass1 = generatedPassword;

                        if( pass1.equals(UserDetails.password)){
                            decrypt();
                            alert.cancel();
                        }
                        else{
                            password.setError("Incorrect password");
                        }
                    }

                });

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.cancel();
                    }
                });

                alert = builder.create();
                alert.show();
                break;
        }


        return true;
    }

    private void decrypt() {
        reference1.removeValue();
        reference2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if (userName.equals(UserDetails.username)) {
                    addMessageBox("You:-\n" + message, 1);
                } else {
                    addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
                }
                layout.removeAllViews();
                reference1.push().setValue(map);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Toast.makeText(this,"Decrypted",Toast.LENGTH_LONG).show();
    }

    public void openCamera(View view){
        Log.d("log","success");
        Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();
        Intent opencamera = new Intent(Chat.this,Opencamera.class);
        startActivity(opencamera);
    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    messageArea.setText(result.get(0));
                }
                break;
        }
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public static SecretKey getSecretEncryptionKey() throws Exception{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128); // The AES key size in number of bits
        SecretKey secKey = generator.generateKey();
        return secKey;
    }

    public static byte[] encryptText(String plainText,SecretKey secKey) throws Exception{
        // AES defaults to AES/ECB/PKCS5Padding in Java 7
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());
        return byteCipherText;
    }

    public static String decryptText(byte[] byteCipherText, SecretKey secKey) throws Exception {
        // AES defaults to AES/ECB/PKCS5Padding in Java 7
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, secKey);
        byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
        return new String(bytePlainText);
    }



}