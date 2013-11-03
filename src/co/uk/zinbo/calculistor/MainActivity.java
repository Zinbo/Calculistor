package co.uk.zinbo.calculistor;

import java.util.HashMap;

import co.uk.zinbo.calculator.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class MainActivity extends Activity {
	
	private String number = "0.0";
	private char[] symbols = {'+', '-', '/', '*', '.'};
	private String previousAnswer = "";
	private boolean answerOnScreen = false;
	private HashMap<CharSequence, String> results = new HashMap<CharSequence, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void dealWithNumber(View view) {
		EditText display = retrieveNumberFromDisplay();
		
	    String numberToAppend = (String) view.getTag();
	    addStringToDisplay(display, numberToAppend);
	}
	
	public void calculateNumber(View view) throws UnknownFunctionException, UnparsableExpressionException {
		try{
			EditText display = retrieveNumberFromDisplay();
			Calculable calc = new ExpressionBuilder(number).build();
			double result = calc.calculate();
			previousAnswer = String.valueOf(result);
			display.setText(previousAnswer);
			answerOnScreen = true;
		}catch(Exception e){
			Toast.makeText(getApplicationContext(), "Exception is unparsable!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void storeAnswer(View view){
		if(answerOnScreen){
			final EditText input = new EditText(MainActivity.this);
			new AlertDialog.Builder(MainActivity.this)
			.setTitle("Name stored answer")
			.setView(input)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					results.put(input.getText().toString(), previousAnswer);
				}
			})
			.setNegativeButton("Cancel", null).show();
		}else{
			Toast.makeText(getApplicationContext(), "Must first have an answer!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void retrieveAnswer(View view){
		final EditText display = retrieveNumberFromDisplay();
		if(results.size() > 0){
			final CharSequence[] items = new CharSequence[results.size()]; 
			Object[] keys = results.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				items[i] = keys[i].toString();
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick answer to retieve");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					addStringToDisplay(display, results.get(items[item]));
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}else{
			Toast.makeText(getApplicationContext(), "No answers stored!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void addManipulator(View view) {
		EditText display = retrieveNumberFromDisplay();
		addSymbolToDisplay(view, display);
	}

	
	public void clear(View view) {
		if(!answerOnScreen){
			EditText display = retrieveNumberFromDisplay();
			removeCharFromDisplay(display);
		}
	}
	
	public void retrievePreviousAnswer(View view){
		if(previousAnswer.length() > 0){
			EditText display = retrieveNumberFromDisplay();
			addStringToDisplay(display, previousAnswer);
		}
	}
	
	public void clearAll(View view){
		EditText display = retrieveNumberFromDisplay();
		display.setText("");
		display.setSelection(0);
		answerOnScreen = false;
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	private EditText retrieveNumberFromDisplay() {
		EditText display = (EditText) findViewById(R.id.display);
		number = display.getText().toString();
		return display;
	}
	
	private void addStringToDisplay(EditText display, String numberToAppend) {
		int cursorPosition = display.getSelectionStart();
		if(answerOnScreen){
			display.setText(numberToAppend);
			display.setSelection(numberToAppend.length());
			answerOnScreen = false;
		}else{
			StringBuffer numberDisplay = new StringBuffer(number);
			numberDisplay.insert(cursorPosition, numberToAppend);
			
			display.setText(numberDisplay.toString());
			display.setSelection(cursorPosition + numberToAppend.length());
		}
	}

	private void addSymbolToDisplay(View view, EditText display) {
		int cursorPosition = display.getSelectionStart();
		String symbolToAppend = (String) view.getTag();
		if(cursorPosition > 0 && cursorPosition < number.length()){
			if(!foundSymbol(number.charAt(cursorPosition - 1)) && !foundSymbol(number.charAt(cursorPosition))){
				addStringToDisplay(display, symbolToAppend);
			}
		}else if(cursorPosition == number.length()){
			if(!foundSymbol(number.charAt(cursorPosition - 1))){
				addStringToDisplay(display, symbolToAppend);
			}
		}else{
			if(!symbolToAppend.equals(!foundSymbol(number.charAt(cursorPosition)))){
				addStringToDisplay(display, symbolToAppend);
			}
		}
	}
	
	private boolean foundSymbol(char displayCharacter){
		for (int i = 0; i < symbols.length; i++) {
			if(displayCharacter == symbols[i]){
				return true;
			}
		}
		return false;
	}
	
	private void removeCharFromDisplay(EditText display) {
		int cursorPosition = display.getSelectionStart();
		if (cursorPosition > 0) {
			StringBuffer numberDisplay = new StringBuffer(number);
			numberDisplay.deleteCharAt(cursorPosition-1);
			display.setText(numberDisplay.toString());
			display.setSelection(cursorPosition - 1);
		}
	}
}
