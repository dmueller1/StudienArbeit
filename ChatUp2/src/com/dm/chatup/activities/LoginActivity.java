package com.dm.chatup.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.dm.chatup.system.AppSystem;
import com.dm.chatup.system.AppSystemOnline;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	String deviceID;
	AppSystem mySystem;
	AppSystemOnline mySystemOnline;
	int userID;
	LinearLayout anmeldeMaske;
    ProgressBar warteKreis;
    TextView txtInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        this.mySystem = AppSystem.getInstance();
        this.mySystemOnline = AppSystemOnline.getInstance();
        this.anmeldeMaske = (LinearLayout) findViewById(R.id.anmeldeMaske);
        this.warteKreis = (ProgressBar) findViewById(R.id.progressBar1);
        this.txtInfo = (TextView) findViewById(R.id.textView4);
        
        anmeldeMaske.setVisibility(View.GONE);
        txtInfo.setText("Daten werden geladen...");
        txtInfo.setVisibility(View.VISIBLE);
        warteKreis.setVisibility(View.VISIBLE);

		TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		deviceID = tManager.getDeviceId();
		
		ATGetDataUserExisting iue = new ATGetDataUserExisting();
		iue.execute(new String[] { deviceID });
		
    }
    
    public void neuAnmelden(View v) {
		
		String vorname = ((EditText) findViewById(R.id.txt_vorname)).getText().toString();
		String nachname = ((EditText) findViewById(R.id.txt_nachname)).getText().toString();

		if (vorname.length() > 0 && nachname.length() > 0) {
			ATAddUser au = new ATAddUser();
			au.execute(new String[] {deviceID, vorname, nachname});
		} else {
			txtInfo.setText("Bitte Vor- und Nachname ausfüllen!");
		}
    }

    private class ATGetDataUserExisting extends AsyncTask<String, Integer, Void> {

    	@Override
		protected void onPreExecute() {
    		txtInfo.setText("Prüfe auf existierenden Benutzer...");
		}
    	
		@Override
		protected Void doInBackground(String... params) {
			
			// User existing???
			int userID = mySystemOnline.isUserExisting(params[0]);
			
			if (userID > -1) {
				publishProgress(new Integer[] {1});
				// If yes, get initial data
				mySystemOnline.getInitialData();
			} else if (userID == -1 ){
				publishProgress(new Integer[] {2});
				this.cancel(true);
			} else {
				publishProgress(new Integer[] {3});
				this.cancel(true);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			txtInfo.setText("Daten erfolgreich geladen. Öffne Chat-Übersicht...");
			Intent i = new Intent(getApplicationContext(), ContactActivity.class);
			startActivity(i);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int status = values[0];
			
			if(status == 1) {
				txtInfo.setText("Benutzer erfolgreich verifiziert. Lade Daten...");
			} else if(status == 2) {
				anmeldeMaske.setVisibility(View.VISIBLE);
				warteKreis.setVisibility(View.GONE);
				txtInfo.setText("Gerät ist noch nicht registriert \nBitte neu anmelden, um die App nutzen zu können...");
		        txtInfo.setVisibility(View.VISIBLE);
			} else {
				warteKreis.setVisibility(View.GONE);
				txtInfo.setText("Fehler beim Abfragen der Daten. \n - Hast du Internet? \n - Läuft der Server? \nBitte App schließen und erneut starten!");
		        txtInfo.setVisibility(View.VISIBLE);
			}
		}
	}
    
    private class ATAddUser extends AsyncTask<String, Boolean, Void> {

    	@Override
		protected void onPreExecute() {
    		warteKreis.setVisibility(View.VISIBLE);
    		txtInfo.setText("Speichere neuen Nutzer in der Datenbank...");
		}
    	
		@Override
		protected Void doInBackground(String... params) {
			boolean success = mySystemOnline.createNewUser(params[0], params[1], params[2]);
			publishProgress(new Boolean[] {success});
			
			if(success) {
				mySystemOnline.getInitialData();
			} else {
				this.cancel(true);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			txtInfo.setText("Daten erfolgreich geladen. Öffne Chat-Übersicht...");
			Intent i = new Intent(getApplicationContext(), ContactActivity.class);
			startActivity(i);
		}

		@Override
		protected void onProgressUpdate(Boolean... values) {
			boolean success = values[0];
			
			if(success) {
				txtInfo.setText("Benutzer erfolgreich hinzugefügt. Lade Daten...");
			} else {
				txtInfo.setText("Fehler beim Anlegen der Daten!");
			}
		}
    	
    	
    }
    
}
