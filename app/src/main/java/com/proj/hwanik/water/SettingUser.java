package com.proj.hwanik.water;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.List;

public class SettingUser extends AppCompatActivity {

    Toolbar toolbar;
    TextView userEmail;
    TextView userNickName;
    EditText userFamilyMember;
    private Button city;
    private Button district;

    int checkedItem = 0;
    int code;
    SignUp signUp=new SignUp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user);

        toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        setTitle("개인정보 수정");

        userEmail = (TextView)findViewById(R.id.userEmail);
        userNickName = (TextView)findViewById(R.id.userNickName);
        userFamilyMember = (EditText)findViewById(R.id.userFamilyMember);
        city=(Button)findViewById(R.id.city);
        district=(Button)findViewById(R.id.district);

        setUserData();
    }

    private void setUserData() {
        userEmail.setText(ParseUser.getCurrentUser().get("email").toString());
        userNickName.setText(ParseUser.getCurrentUser().getUsername());
        userFamilyMember.setText(ParseUser.getCurrentUser().get("FamilyMember").toString());
        city.setText(ParseUser.getCurrentUser().get("city").toString());
        district.setText(ParseUser.getCurrentUser().get("district").toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present. 
        getMenuInflater().inflate(R.menu.menu_setting_user, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.update) {
            update();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void update() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    list.get(0).put("city",city.getText().toString());
                    list.get(0).put("district",district.getText().toString());
                    list.get(0).put("FamilyMember",userFamilyMember.getText().toString());
                    list.get(0).put("code",code);
                    list.get(0).saveInBackground();
                    finish();
                    // The query was successful.
                } else {
                    // Something went wrong.
                }
            }
        });
    }

    public void choiceCity(View view) {

        final int temp = checkedItem;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("도시 및 도를 선택하세요");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);

        /** 배열을 선택 항목으로 배치 */
        /*두 번째 값인 checkedItem 는 초기에 선택되어 있기 위한 위치값이다.
        * 이벤트에 의해서 checkedItem 값에 사용자의 선택값이 저장되어 있기 때문에,
        * 최종적으로 사용자가 선택했던 항목값이 Dialog 에서 선택되게 된다.*/
        builder.setSingleChoiceItems(signUp.cities, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), signUp.cities[which], Toast.LENGTH_SHORT).show();
                /** 전역변수에 사용자가 선택한 값을 복사 */
                checkedItem = which;
            }
        });

        /** 긍정의 의미를 갖는 버튼 */
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), signUp.cities[checkedItem], Toast.LENGTH_SHORT).show();
                city.setText(signUp.cities[checkedItem]);
            }
        });

        /** 부정의 의미를 갖는 버튼 */
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /** 백업한 값을 돌려 놓는다. */
                checkedItem = temp;
            }
        });
        builder.create();
        builder.show();
    }

    public void choiceDistrict(View view) {
        /** 이전 선택값을 임시로 백업한다. */
        final int temp = checkedItem;
        String choicedCity=city.getText().toString();
        String []districts=new String[30];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("구 및 군을 선택해주세요");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);

        switch(choicedCity){
            case "서울특별시":
                districts=signUp.district1; district.setText("");
                break;
            case "부산광역시":
                districts=signUp.district2; district.setText("");
                break;
            case "대구광역시":
                districts=signUp.district3; district.setText("");
                break;
            case "인천광역시":
                districts=signUp.district4; district.setText("");
                break;
            case "광주광역시":
                districts=signUp.district5; district.setText("");
                break;
            case "대전광역시":
                districts=signUp.district6; district.setText("");
                break;
            case "울산광역시":
                districts=signUp.district7; district.setText("");
                break;
            case "세종특별자치시":
                districts=signUp.district8; district.setText("");
                break;
            case "경기도":
                districts=signUp.district9; district.setText("");
                break;
            case "강원도":
                districts=signUp.district10; district.setText("");
                break;
            case "충청북도":
                districts=signUp.district11; district.setText("");
                break;
            case "충청남도":
                districts=signUp.district12; district.setText("");
                break;
            case "전라북도":
                districts=signUp.district13; district.setText("");
                break;
            case "전라남도":
                districts=signUp.district14; district.setText("");
                break;
            case "경상북도":
                districts=signUp.district15; district.setText("");
                break;
            case "경상남도":
                districts=signUp.district16; district.setText("");
                break;
            case "제주특별자치도":
                districts=signUp.district17; district.setText("");
                break;
        }
        /** 배열을 선택 항목으로 배치 */
        /*두 번째 값인 checkedItem 는 초기에 선택되어 있기 위한 위치값이다.
        * 이벤트에 의해서 checkedItem 값에 사용자의 선택값이 저장되어 있기 때문에,
        * 최종적으로 사용자가 선택했던 항목값이 Dialog 에서 선택되게 된다.*/
        final String[] finalDistricts = districts;
        builder.setSingleChoiceItems(districts, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), finalDistricts[which], Toast.LENGTH_SHORT).show();
                /** 전역변수에 사용자가 선택한 값을 복사 */
                checkedItem = which;
            }
        });

        /** 긍정의 의미를 갖는 버튼 */
        final String[] finalDistricts1 = districts;
        final String[] finalDistricts2 = districts;
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), finalDistricts1[checkedItem], Toast.LENGTH_SHORT).show();
                district.setText(finalDistricts2[checkedItem]);

                takeCode(city.getText().toString(), district.getText().toString());

            }
        });

        /** 부정의 의미를 갖는 버튼 */
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /** 백업한 값을 되돌려 놓는다. */
                checkedItem = temp;
            }
        });
        builder.create();
        builder.show();
    }
    private void takeCode(String city, String district) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("AddressCode");
        query.whereEqualTo("sido", city);
        query.whereEqualTo("gungu", district);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> codeList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + codeList.size() + " scores");
                    code = (int) codeList.get(0).get("code");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
}
