package com.example.hwanik.water;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

public class SignUp extends AppCompatActivity {

    private MaterialEditText userEmail;
    private MaterialEditText userPwd;
    private MaterialEditText userNickName;
    private MaterialEditText userFamilyMember;
    private Button btnSignUp;
    private Button city;
    private Button district;

    /** 팝업창에 보여질 목록에 대한 비열 */
    String[] cities = {"서울특별시", "부산광역시", "대구광역시","인천광역시","광주광역시","대전광역시","울산광역시","세종특별자치시",
                        "경기도","강원도","충청북도","충청남도","전라북도","전라남도","경상북도","경상남도","제주특별자치도"};
    //서울특별시 구
    String[] district1={"", "강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구",
                        "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"};
    //부산광역시 구
    String[] district2={"", "강서구", "금정구", "기장군", "남구", "동구", "동래구", "부산진구", "북구", "사상구", "사하구",
                        "서구", "수영구", "연제구", "영도구", "중구", "해운대구" };
    //대구광역시 구
    String[] district3={"", "남구", "달서구", "달성군", "동구", "북구", "서구", "수성구", "중구" };
    //인천광역시 구
    String[] district4={"", "강화군", "계양구", "남구", "남동구", "동구", "부평구", "서구", "서구검단출장", "연수구", "옹진군", "중구", "중구영종출장", "중구용유출장"};
    //광주광역시 구
    String[] district5={"", "광산구", "남구", "동구", "북구", "서구" };
    //대전광역시 구
    String[] district6={"", "대덕구", "동구", "서구", "유성구", "중구" };
    //울산광역시 구
    String[] district7={"", "남구", "동구", "북구", "울주군", "중구" };
    //세종특별자치시
    String[] district8={""};
    //경기도
    String[] district9={"", "가평군", "고양시", "고양시 덕양구", "고양시 일산동구", "고양시 일산서구", "과천시", "광명시", "광주시", "구리시", "군포시",
                        "김포시", "남양주시", "동두천시", "부천시", "부천시 소사구", "부천시 오정구", "부천시 원미구", "성남시", "성남시 분당구", "성남시 수정구", "성남시 중원구",
                        "수원시", "수원시 권선구", "수원시 영통구", "수원시 장안구", "수원시 팔달구", "시흥시", "안산시", "안산시 단원구", "안산시 상록구", "안성시", "안양시", "안양시 동안구",
                        "안양시 만안구", "양주시", "양평군", "여주시", "연천군", "오산시", "용인시", "용인시 기흥구", "용인시 수지구", "용인시 처인구", "의왕시", "의정부시", "이천시", "파주시",
                        "평택시", "포천시", "하남시", "화성시"};
    //강원도
    String[] district10={"", "강릉시", "고성군", "동해시", "삼척시", "속초시", "양구군", "양양군", "영월군", "원주시", "인제군", "정선군", "철원군",
                        "춘천시", "태백시", "평창군", "홍천군", "화천군", "횡성군" };
    //충청북도
    String[] district11={"", "괴산군", "단양군", "보은군", "영동군", "옥천군", "음성군", "제천시", "증평군", "진천군", "청원군", "청주시",
                        "청주시 상당구", "청주시 흥덕구", "충주시" };
    //충청남도
    String[] district12={"", "계룡시", "공주시", "금산군", "논산시", "당진시", "보령시", "부여군", "서산시", "서천군", "아산시", "예산군",
                        "천안시", "천안시 동남구", "천안시 서북구", "청양군", "태안군", "홍성군" };
    //전라북도
    String[] district13={"", "고창군", "군산시", "김제시", "남원시", "무주군", "부안군", "순창군", "완주군", "익산시", "익산시함열출", "임실군", "장수군", "전주시",
                        "전주시 덕진구", "전주시 완산구", "전주시효자출", "정읍시", "진안군" };
    //전라남도`
    String[] district14={"", "강진군", "고흥군", "곡성군", "광양시", "구례군", "나주시", "담양군", "목포시", "무안군", "보성군", "순천시", "신안군", "여수시",
                        "영광군", "영암군", "완도군", "장성군", "장흥군", "진도군", "함평군", "해남군", "화순군" };
    //경상북도`
    String[] district15={"", "경산시", "경주시", "고령군", "구미시", "군위군", "김천시", "문경시", "봉화군", "상주시", "성주군", "안동시", "영덕군", "영양군",
                        "영주시", "영천시", "예천군", "울릉군", "울진군", "의성군", "청도군", "청송군", "칠곡군", "포항시", "포항시 남구", "포항시 북구" };
    //경상남도
    String[] district16={"", "거제시", "거창군", "고성군", "김해시", "남해군", "밀양시", "사천남양출장", "사천시", "산청군", "양산시", "의령군", "진주시", "창녕군",
                        "창원시", "창원시 마산합포구", "창원시 마산회원구", "창원시 성산구", "창원시 의창구", "창원시 진해구", "통영시", "하동군", "함안군", "함양군", "합천군" };
    //제주도
    String[] district17={"", "제주시", "서귀포시" };
    /** 선택 상태를 관리하기 위한 전역변수 */
    int checkedItem = 0;
    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        city=(Button)findViewById(R.id.city);
        city.setText("서울특별시");
        district=(Button)findViewById(R.id.district);
        district.setText("");

        userEmail=(MaterialEditText)findViewById(R.id.userEmail);
        userPwd=(MaterialEditText)findViewById(R.id.userPwd);
        userNickName=(MaterialEditText)findViewById(R.id.userNickName);
        userFamilyMember=(MaterialEditText)findViewById(R.id.userFamilyMember);

        btnSignUp=(Button)findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ParseUser user = new ParseUser();
                user.setUsername(userNickName.getText().toString());
                user.setPassword(userPwd.getText().toString());
                user.setEmail(userEmail.getText().toString());
                user.put("city", city.getText().toString());
                user.put("district", district.getText().toString());
                user.put("FamilyMember", userFamilyMember.getText().toString());
                user.put("code",code);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(SignUp.this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d("error", String.valueOf(e));
                            Toast.makeText(SignUp.this, "이미 가입된 회원입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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
        Toast.makeText(SignUp.this,String.valueOf(code),Toast.LENGTH_SHORT).show();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void choiceCity(View view) {
        /** 이전 선택값을 임시로 백업한다. */
        final int temp = checkedItem;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("도시 및 도를 선택하세요");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);

        /** 배열을 선택 항목으로 배치 */
        /*두 번째 값인 checkedItem 는 초기에 선택되어 있기 위한 위치값이다.
        * 이벤트에 의해서 checkedItem 값에 사용자의 선택값이 저장되어 있기 때문에,
        * 최종적으로 사용자가 선택했던 항목값이 Dialog 에서 선택되게 된다.*/
        builder.setSingleChoiceItems(cities, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), cities[which], Toast.LENGTH_SHORT).show();
                /** 전역변수에 사용자가 선택한 값을 복사 */
                checkedItem = which;
            }
        });

        /** 긍정의 의미를 갖는 버튼 */
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), cities[checkedItem], Toast.LENGTH_SHORT).show();
                city.setText(cities[checkedItem]);
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
                districts=district1; district.setText("");
                break;
            case "부산광역시":
                districts=district2; district.setText("");
                break;
            case "대구광역시":
                districts=district3; district.setText("");
                break;
            case "인천광역시":
                districts=district4; district.setText("");
                break;
            case "광주광역":
                districts=district5; district.setText("");
                break;
            case "대전광역시":
                districts=district6; district.setText("");
                break;
            case "울산광역시":
                districts=district7; district.setText("");
                break;
            case "세종특별자치시":
                districts=district8; district.setText("");
                break;
            case "경기도":
                districts=district9; district.setText("");
                break;
            case "강원도":
                districts=district10; district.setText("");
                break;
            case "충청북도":
                districts=district11; district.setText("");
                break;
            case "충청남도":
                districts=district12; district.setText("");
                break;
            case "전라북도":
                districts=district13; district.setText("");
                break;
            case "전라남도":
                districts=district14; district.setText("");
                break;
            case "경상북도":
                districts=district15; district.setText("");
                break;
            case "경상남도":
                districts=district16; district.setText("");
                break;
            case "제주특별자치도":
                districts=district17; district.setText("");
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

                takeCode(city.getText().toString(),district.getText().toString());

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
}
