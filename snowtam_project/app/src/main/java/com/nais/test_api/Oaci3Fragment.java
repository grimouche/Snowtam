package com.nais.test_api;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.squareup.picasso.Picasso;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Oaci3Fragment extends Fragment implements View.OnClickListener {

    View view;
    private TextView resultsTextView;
    private Button go_map;
    private Button view_snowtam;
    private Button view_details;
    private TextView informationsAirport;
    private ImageView meteo;
    private ImageView flag;
    private LinearLayout detailsAirportLayout;
    private TableRow row;

    private String apikey = "70450310-1ac2-11ea-ad71-81e267125d1c";
    private String snowtam = "";
    private String oaci_recup = "test";
    private String name_airport = "";
    private String longitude="0";
    private String latitude="0";
    private String country;
    double LATI;
    double LONGI;

    public Oaci3Fragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_oaci3,
                container, false);

        RecupApiActivity test = (RecupApiActivity) getActivity();

        //appel de la fct sendData pour obtenir le code oaci rentré dans MainActivity
        oaci_recup = test.sendData3();

        resultsTextView = view.findViewById(R.id.resultsTextView3);
        informationsAirport = view.findViewById(R.id.infosAirport3);
        detailsAirportLayout = view.findViewById(R.id.detailsView3);


        go_map = (Button)view.findViewById(R.id.BoutonMap3);
        go_map.setOnClickListener(this);

        view_snowtam = (Button)view.findViewById(R.id.snowtamButton3);
        view_details = (Button)view.findViewById(R.id.detailsButton3);

        meteo = (ImageView)view.findViewById(R.id.meteoImage3);
        flag = (ImageView)view.findViewById(R.id.flagImage3);
        row = (TableRow)view.findViewById(R.id.tableRow2_3);

        detailsAirportLayout.setVisibility(view.GONE);

        //si pas de code rentré en première page on n'effectue pas la requête
        if(oaci_recup.isEmpty())
        {
            resultsTextView.setText(R.string.no_oaci);
            informationsAirport.setText(R.string.no_oaci);

            go_map.setVisibility(view.GONE);
            row.setVisibility(view.GONE);
        }
        else {
            //on récupère le dernier snowtam en date pour le code oaci rentré

            RequestQueue rq = Volley.newRequestQueue(getActivity().getApplicationContext());

            String url = "https://v4p4sz5ijk.execute-api.us-east-1.amazonaws.com/anbdata/states/notams/notams-realtime-list?api_key="+ apikey +"&format=json&criticality=&locations=" + oaci_recup;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Do something with the response
                    try {
                        JSONArray obj = new JSONArray(response);

                        for (int i = 0; i < obj.length(); i++) {
                            String jsonrecup = obj.getJSONObject(i).getString("all");

                            if(jsonrecup.contains("SNOWTAM")){
                                snowtam = jsonrecup;
                                try {
                                    String date_string = snowtam.substring(snowtam.indexOf("B)") + 3, snowtam.indexOf("C)"));
                                    SimpleDateFormat dateformat = new SimpleDateFormat("MMddHHmm");
                                    Date date = dateformat.parse(date_string);
                                    dateformat.applyPattern("dd MMMM HH:mm");
                                    String date_snowtam = dateformat.format(date);

                                    informationsAirport.append("\nCréation du snowtam : "+date_snowtam);
                                }catch (ParseException ex){}

                                resultsTextView.setText(snowtam);
                                break;
                            }
                        }

                        if(snowtam.equals("")){
                            resultsTextView.setText(R.string.no_results);
                        }

                    } catch (JSONException ex) {
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            resultsTextView.setText(R.string.erreur);
                        }
                    });

            rq.add(stringRequest);

            /**** Récupérer la longitude, la latitude et le nom de l'aéroport ****/

            String urlLocation= "https://v4p4sz5ijk.execute-api.us-east-1.amazonaws.com/anbdata/airports/locations/doc7910?api_key="+apikey+"&airports="+ oaci_recup +"&format=json";

            StringRequest stringRequestLocation = new StringRequest(Request.Method.GET, urlLocation,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            // Do something with the response
                            try{

                                JSONArray obj = new JSONArray(response);

                                for ( int i=0; i< obj.length(); i++) {
                                    longitude = obj.getJSONObject(i).getString("Longitude");
                                    latitude = obj.getJSONObject(i).getString("Latitude");

                                    getWeather(longitude, latitude);

                                    LATI = Double.parseDouble(latitude);
                                    LONGI = Double.parseDouble(longitude);

                                    name_airport = obj.getJSONObject(i).getString("Location_Name");
                                    country = obj.getJSONObject(i).getString("State_Name");
                                    informationsAirport.append("\n" + country + "\n" + name_airport);

                                    String country_code = obj.getJSONObject(i).getString("ctry_code");
                                    String country_flag = country_code.toLowerCase();

                                    Uri uri = Uri.parse("https://restcountries.eu/data/"+country_flag+".svg");

                                    GlideToVectorYou
                                            .init()
                                            .with(getActivity())
                                            .withListener(new GlideToVectorYouListener() {
                                                @Override
                                                public void onLoadFailed() {
                                                }

                                                @Override
                                                public void onResourceReady() {
                                                }
                                            })
                                            .load(uri, flag);


                                }
                            }  catch (JSONException ex){}
                        }

                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            rq.add(stringRequestLocation);
        }

        view_snowtam.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                detailsAirportLayout.setVisibility(view.GONE);
                resultsTextView.setVisibility(view.VISIBLE);
            }
        });

        view_details.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                resultsTextView.setVisibility(view.GONE);
                detailsAirportLayout.setVisibility(view.VISIBLE);
            }
        });

        return view;
    }

    private void getWeather(String lat, String lon){

        RequestQueue rq = Volley.newRequestQueue(getActivity().getApplicationContext());
        String urlWeather= "http://api.openweathermap.org/data/2.5/weather?appid=22b10866f3495dcf304ca47f7d97d65b&lat="+lat+"&lon="+lon;

        StringRequest stringRequestWeather = new StringRequest(Request.Method.GET, urlWeather,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try{

                            JSONObject obj = new JSONObject(response);

                            JSONArray testArray = obj.getJSONArray("weather");

                            for (int i = 0; i < testArray.length(); i++) {
                                String icon = testArray.getJSONObject(i).getString("icon");
                                String url = "http://openweathermap.org/img/wn/"+icon+"@2x.png";
                                Picasso.get().load(url).into(meteo);
                            }

                        }  catch (JSONException ex){}
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                    }
                });
        rq.add(stringRequestWeather);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), Maps1Activity.class);
        intent.putExtra("LATI" , LATI);
        intent.putExtra("LONGI" , LONGI);
        intent.putExtra("AIRPORT_NAME", name_airport);
        intent.putExtra("COUNTRY", country);
        startActivity(intent);
    }
}
