package com.example.android.pokemon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView list_view;

    ArrayList<HashMap<String, String>> pokemonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pokemonList = new ArrayList<>();
        list_view = (ListView) findViewById(R.id.list);

        new GetPokemon().execute();
    }

    private class GetPokemon extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = "https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json";


            String jsonString = "";
            try {
                // TODO: make a request to the URL
                URL urlObject = createUrl(url);
                jsonString = sh.makeHttpRequest(urlObject);

            } catch (IOException e) {
                return null;
            }

            Log.e(TAG, "Response from url: " + jsonString);
            if (jsonString != null) {
                try {
                    //TODO: Create a new JSONObject
                    JSONObject jsonRootObj = new JSONObject(jsonString);

                    // TODO: Get the JSON Array node and name it "pokemons"
                    JSONArray pokemons = jsonRootObj.getJSONArray("pokemon");

                    // looping through all Contacts
                    for (int i = 0; i < pokemons.length(); i++) {
                        //TODO: get the JSONObject and its three attributes
                        JSONObject currentObjArrayPosition = pokemons.getJSONObject(i);

                        String name = currentObjArrayPosition.getString("name");
                        String id = currentObjArrayPosition.getString("id");
                        String candy = currentObjArrayPosition.getString("candy");

                        // tmp hash map for a single pokemon
                        HashMap<String, String> pokemon = new HashMap<>();

                        // add each child node to HashMap key => value
                        pokemon.put("name", name);
                        pokemon.put("id", id);
                        pokemon.put("candy", candy);

                        // adding a pokemon to our pokemon list
                        pokemonList.add(pokemon);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //i got this error when i had not added my app internet permission in manifest file or if my phone is in airplane mode
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                return null;
            }
            return url;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, pokemonList,
                    R.layout.list_item, new String[]{"name", "id", "candy"},
                    new int[]{R.id.name, R.id.id, R.id.candy});
            list_view.setAdapter(adapter);
        }
    }
}
