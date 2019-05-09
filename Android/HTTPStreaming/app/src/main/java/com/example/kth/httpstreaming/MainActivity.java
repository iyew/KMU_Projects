// http://developer88.tistory.com/58
// http://webnautes.tistory.com/829
// http://webnautes.tistory.com/1159
/*
** 권태희: 로그인 처리, 뮤직 리스트 및 플레이어 처리
** 박민수: UI 및 서버 담당
***************************************************************************************************
** 테스트용 ID 및 Password
** ID: 5171515  Password: 1q2w3e4r
** ID: 22       Password: 22
*/
package com.example.kth.httpstreaming;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    private String userID;
    ListView musicListView;
    ArrayList<HashMap<String, String>> musicArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicListView = (ListView) findViewById(R.id.music_list);
        musicArrayList = new ArrayList<>();

        intent = getIntent();
        userID = intent.getStringExtra("USER_ID");

        MusicListTask musicListTask = new MusicListTask();
        musicListTask.execute("http://192.168.219.105:8001/music_list.php");

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent musicIntent = new Intent(getApplicationContext(), MusicPlayerAcitivty.class);
                musicIntent.putExtra("MUSIC_TITLE", musicArrayList.get(i).get("music_title"));
                musicIntent.putExtra("MUSIC_ARTIST", musicArrayList.get(i).get("music_artist"));
                startActivity(musicIntent);
            }
        });
    }

    private class MusicListTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.connect();

                StringBuilder sb = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String json;
                while((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }

                bufferedReader.close();
                return sb.toString().trim();
            } catch(Exception e) {
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("music_list");

                for(int i=0; i<jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    String music_title = item.getString("music_title");
                    String music_artist = item.getString("music_artist");
                    String music_owner = item.getString("music_owner");

                    HashMap<String, String> hashMap = new HashMap<>();
                    if(music_owner.equals(userID)) {
                        hashMap.put("music_title", music_title);
                        hashMap.put("music_artist", music_artist);
                        musicArrayList.add(hashMap);
                    }
                }

                ListAdapter adapter = new SimpleAdapter(MainActivity.this, musicArrayList, R.layout.item_list,
                        new String[]{"music_title", "music_artist"},
                        new int[]{R.id.text_music_title, R.id.text_music_artist});
                musicListView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
