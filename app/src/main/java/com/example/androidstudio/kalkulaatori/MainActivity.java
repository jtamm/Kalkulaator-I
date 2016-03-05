package com.example.androidstudio.kalkulaatori;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    AutoResizeTextView calcText;

    Button bReset;
    Button bBack;
    Button bDivide;
    Button bMultiple;
    Button bSubtract;
    Button bAdd;
    Button bResult;
    Button bComma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        calcText = (AutoResizeTextView) findViewById(R.id.calc_text);
        bReset = (Button)findViewById(R.id.button_reset);
        bBack = (Button) findViewById(R.id.button_back);
        bDivide = (Button) findViewById(R.id.button_divide);
        bMultiple = (Button) findViewById(R.id.button_multiple);
        bSubtract = (Button) findViewById(R.id.button_subtract);
        bAdd = (Button) findViewById(R.id.button_add);
        bResult = (Button) findViewById(R.id.button_result);
        bComma = (Button) findViewById(R.id.button_comma);
        calcText.setText(Data.calcText);
        checkValid();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void calcButtonClicked(View view){
        Button b = (Button) view;
        calcCommand(b.getText().toString());
        Data.calcText = calcText.getText().toString();
        checkValid();
    }

    private void checkValid(){
        bSubtract.setEnabled(true);
        String calcTextData = calcText.getText().toString().trim();
        if(calcTextData.equals("")){
            bReset.setEnabled(false);
            bBack.setEnabled(false);
            bComma.setEnabled(false);
            bResult.setEnabled(false);
            bDivide.setEnabled(false);
            bMultiple.setEnabled(false);
            bAdd.setEnabled(false);
        }
        else{
            bReset.setEnabled(true);
            bBack.setEnabled(true);
            bDivide.setEnabled(true);
            bMultiple.setEnabled(true);
            bAdd.setEnabled(true);
            bResult.setEnabled(false);
            bComma.setEnabled(true);
            Character lastChar = calcTextData.charAt(calcTextData.length() - 1);
            if(lastChar == '-'){
                bSubtract.setEnabled(false);
                if(calcTextData.equals("-")){
                    bDivide.setEnabled(false);
                    bMultiple.setEnabled(false);
                    bAdd.setEnabled(false);
                }
                else if(calcTextData.length() > 1){
                    Character c = calcTextData.charAt(calcTextData.length() - 2);
                    if(c == '('){
                        bDivide.setEnabled(false);
                        bMultiple.setEnabled(false);
                        bAdd.setEnabled(false);
                    }
                }
                bComma.setEnabled(false);
            }
            else if(lastChar == '+'){
                bAdd.setEnabled(false);
                bComma.setEnabled(false);
            }
            else if(lastChar == '/'){
                bDivide.setEnabled(false);
                bComma.setEnabled(false);
            }
            else if(lastChar == '*'){
                bMultiple.setEnabled(false);
                bComma.setEnabled(false);
            }
            else if(lastChar == ','){
                bComma.setEnabled(false);
                bDivide.setEnabled(false);
                bMultiple.setEnabled(false);
                bSubtract.setEnabled(false);
                bAdd.setEnabled(false);
            }
            else if(lastChar == '('){
                bDivide.setEnabled(false);
                bMultiple.setEnabled(false);
                bAdd.setEnabled(false);
                bComma.setEnabled(false);
            }
            else if(lastChar == ')'){
                bComma.setEnabled(false);
                bResult.setEnabled(!Data.calcAnswered);
            }
            else if(Character.isLetter(lastChar)){
                bDivide.setEnabled(false);
                bMultiple.setEnabled(false);
                bAdd.setEnabled(false);
                bSubtract.setEnabled(false);
                bComma.setEnabled(false);
            }
            else{
                bResult.setEnabled(!Data.calcAnswered);
                bComma.setEnabled(!Data.calcAnswered);
                for(int i = calcTextData.length() -1; i > 0; i--){
                    Character c = calcTextData.charAt(i);
                    if(c == '/' || c == '*' || c == '-' || c == '+' || c == '(' || c == ')'){
                        break;
                    }
                    else if(c == ','){
                        bComma.setEnabled(false);
                        break;
                    }
                }
            }
        }
    }

    private String doubleToString(double d){
        if(d == (long)d){
            return String.format("%d",(long)d);
        }
        else{
            return String.format("%s", d);
        }
    }

    public void calcCommand(String command){
        String calcTextData = calcText.getText().toString();
        if(command.equals("Back")){
            if(calcTextData.length() != 0){
                if(Data.calcAnswered){
                    String[] d1 = calcTextData.split("=");
                    calcText.setText(d1[0]);
                    Data.calcAnswered = false;
                }
                else{
                    if(calcTextData.length() == 1){
                        calcText.setText(" ");
                        calcText.setText("");
                    }
                    else{
                        calcText.setText(calcTextData.substring(0, calcTextData.length() - 1));
                    }
                }
            }
        }
        else if(command.equals("C")){
            calcText.setText(" ");
            calcText.setText("");
            Data.calcAnswered = false;
        }
        else if(command.equals("=")){
            if(!Data.calcAnswered){
                try {
                    MathNode mathNode = MathNode.parse(calcTextData.replace('/', ':').replace(',','.'));
                    //String.format("%s=%s",calcTextData, mathNode.answer())
                    double answeredValue = mathNode.answer();
                    if(Double.isInfinite(answeredValue)){
                        if(answeredValue == Double.POSITIVE_INFINITY){
                            calcText.setText(String.format("%s=%s",calcTextData, "\u221E"));
                        }
                        else{
                            calcText.setText(String.format("%s=%s",calcTextData, "-\u221E"));
                        }
                    }
                    else{
                        calcText.setText(String.format("%s=%s",calcTextData, doubleToString(answeredValue).replace('.',',')));
                    }
                }catch (Exception e) {
                    calcText.setText(String.format("%s=%s",calcTextData, "error"));
                }
                Data.calcAnswered = true;
            }
        }
        else if(command.equals("/") || command.equals("*") || command.equals("-") || command.equals("+")){
            if(Data.calcAnswered){
                String[] d = calcTextData.split("=");
                calcText.setText(d[1] + command);
                Data.calcAnswered = false;
            }
            else{
                if(calcTextData.length() != 0){
                    Character lastChar = calcTextData.charAt(calcTextData.length() - 1);
                    if(lastChar == '/' || lastChar == '*' || lastChar == '-' || lastChar == '+'){
                        calcText.setText(calcTextData.substring(0,calcTextData.length()-1) + command);
                    }
                    else{
                        calcText.setText(calcTextData + command);
                    }
                }
                else if(command.equals("-")){
                    calcText.setText(calcTextData + command);
                }
            }
        }
        else{
            if(Data.calcAnswered){
                calcText.setText(command);
                Data.calcAnswered = false;
            }
            else{
                calcText.setText(calcTextData + command);
            }
        }
    }
}
