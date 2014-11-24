package com.cutonala.practica6;

import java.io.File;
import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity {

	private static final String TAG= MainActivity.class.getName();
	private MediaRecorder recorder;
	private boolean recording;
	// are we currently recording?
	// variables for GUI
	private ToggleButton recordButton;
	private Button saveButton;
	private Button deleteButton;
	private Button viewSavedRecordingsButton;
	
	// called when the activity is first created
	@Override
	public void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	// set the Activity's layout
	// get the Activity's Buttons and VisualizerView
	recordButton = (ToggleButton) findViewById(R.id.recordButton);
	saveButton = (Button) findViewById(R.id.saveButton);
	saveButton.setEnabled(false);
	deleteButton = (Button) findViewById(R.id.deleteButton);
	deleteButton.setEnabled(false);
	viewSavedRecordingsButton =(Button) findViewById(R.id.viewSavedRecordingsButton);
	// register listeners
	saveButton.setOnClickListener(saveButtonListener);
	deleteButton.setOnClickListener(deleteButtonListener);
	viewSavedRecordingsButton.setOnClickListener(viewSavedRecordingsListener);
	}
	
	 // create the MediaRecorder
	@Override
	protected void onResume(){
	super.onResume();
	// register recordButton's listener
	recordButton.setOnCheckedChangeListener(recordButtonListener);
	}
	// end method onResume
	// release the MediaRecorder
	@Override
	protected void onPause() {
	super.onPause();
	recordButton.setOnCheckedChangeListener(null);// remove listener
	if
	(recorder !=null){
	recordButton.setChecked(false);// reset recordButton
	viewSavedRecordingsButton.setEnabled(true);
	recorder.release();// enable
	recording =false;// we are no longer recording
	recorder = null;
	((File) deleteButton.getTag()).delete();// delete the temp file
		}// end if
	}// end method onPause
	
	 // starts/stops a recording
	OnCheckedChangeListener recordButtonListener = new OnCheckedChangeListener() {
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
	if (isChecked) {
		saveButton.setEnabled(false);// disable saveButton
		deleteButton.setEnabled(false);// disable deleteButton
		viewSavedRecordingsButton.setEnabled(false);// disable 
	
			// create MediaRecorder and configure recording options
			if(recorder == null)
			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			recorder.setAudioEncodingBitRate(16);
			recorder.setAudioSamplingRate(44100);
			try{
				File tempFile = File.createTempFile("VoiceRecorder",".3gp", getExternalFilesDir(null));
			
				// store File as tag for saveButton and deleteButton
				saveButton.setTag(tempFile);
				deleteButton.setTag(tempFile);
				
				recorder.setOutputFile(tempFile.getAbsolutePath());
				recorder.prepare();// prepare to record
				recorder.start();// start recording 
				recording = true;// we are currently recording
		
				}// end try
				catch (IllegalStateException e)
				{
					Log.e(TAG, e.toString());
				}// end catch
				catch(IOException e)
				{
					Log.e(TAG, e.toString());
				}// end catch
			}// end if
			else
			{
				recorder.stop();// stop recording
				recorder.reset();// reset the MediaRecorder
				recording = false;// we are no longer recording
				saveButton.setEnabled(true);// enable saveButton
				deleteButton.setEnabled(true);// enable deleteButton
				recordButton.setEnabled(false);// disable recordButton
			 }// end else
		}// end method onCheckedChanged
	};// end OnCheckedChangedListener

	// saves a recording
	OnClickListener saveButtonListener = new OnClickListener() {
		@Override
		public void onClick(final View v) { // get a reference to the LayoutInflater service
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);// inflate name_edittext.xml to create an EditText
			View view = inflater.inflate(R.layout.nombre_grabacion, null);
			final EditText nameEditText = (EditText) view.findViewById(R.id.nameEdit); // create an input dialog to get recording name from user
			AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);
			inputDialog.setView(view); // set the dialog's custom View
			inputDialog.setTitle(R.string.dialog_set_name_title);
			inputDialog.setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
			
				public void onClick(DialogInterface dialog, int which) { // create a SlideshowInfo for a new slideshow
				String name = nameEditText.getText().toString().trim();
				
					if(name.length() !=0){ // create Files for temp file and new filename
						File tempFile = (File) v.getTag();
						File newFile = new File(getExternalFilesDir(null).getAbsolutePath() + File.separator + name + ".3gp");
						tempFile.renameTo(newFile);// rename the file 
						saveButton.setEnabled(false);// disable 
						deleteButton.setEnabled(false);// disable
						recordButton.setEnabled(true);// enable
						viewSavedRecordingsButton.setEnabled(true);// enable
					}// end if
					else
					{// display message that slideshow must have a name
						Toast message = Toast.makeText(MainActivity.this, R.string.message_name, Toast.LENGTH_SHORT);
						message.setGravity(Gravity.CENTER, message.getXOffset() /2, message.getYOffset() /2);
						message.show();// display the Toast
					}// end else
				}// end method onClick
			}// end anonymous inner class
		);// end call to setPositiveButton
			
		inputDialog.setNegativeButton(R.string.button_cancel, null);
		inputDialog.show();
		}// end method onClick
	};// end OnClickListener 
	
		// deletes the temporary recording
		OnClickListener deleteButtonListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
		// create an input dialog to get recording name from user
		AlertDialog.Builder confirmDialog = new AlertDialog.Builder(MainActivity.this);
		confirmDialog.setTitle(R.string.dialog_confirm_title);
		confirmDialog.setMessage(R.string.dialog_confirm_message);
		confirmDialog.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
			
				public void onClick(DialogInterface dialog, int which) {
				saveButton.setEnabled(false);// disable
				deleteButton.setEnabled(false);// disable
				recordButton.setEnabled(true);// enable
				viewSavedRecordingsButton.setEnabled(true);// enable
				}// end method onClick
			}// end anonymous inner class
			);// end call to setPositiveButton
			confirmDialog.setNegativeButton(R.string.button_cancel, null);
			confirmDialog.show();
			recordButton.setEnabled(true);// enable recordButton
			}// end method onClick
		}; 
		
				// launch Activity to view saved recordings
				OnClickListener viewSavedRecordingsListener = new OnClickListener() {
				@Override
				public void onClick(View v) { // launch the SaveRecordings Activity
				Intent intent = new Intent(MainActivity.this, GuardarActivity.class);
				startActivity(intent);
			}// end method onClick
		};// end OnClickListener
				// end class VoiceRecorder

}
