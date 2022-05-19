package com.example.appinvoicing;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Actividad_producto extends AppCompatActivity {
    EditText nombre, referencia, precio, stock;
    Button btnadd, btnsearch, btnupdate, btndelete, btnlist;
    producto producto = new producto();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_producto);

        nombre = findViewById(R.id.etnombre);
        referencia = findViewById(R.id.etreferencia);
        precio = findViewById(R.id.etprecio);
        stock = findViewById(R.id.etstock);
        btnadd = findViewById(R.id.btnadd);
        btnsearch = findViewById(R.id.btnsearch);
        btnupdate = findViewById(R.id.btnupdate);
        btndelete = findViewById(R.id.btndelete);
        btnlist = findViewById(R.id.btnlist);

        // eventos de los botones

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                producto = new producto();
                addAndUpdateProducto(null, nombre.getText().toString(), referencia.getText().toString(), precio.getText().toString(), stock.getText().toString());
            }
        });

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchProducto(referencia.getText().toString());
            }
        });

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndUpdateProducto(producto.getId(), nombre.getText().toString(), referencia.getText().toString(), precio.getText().toString(), stock.getText().toString());
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProducto(producto.getId());
            }
        });
    }

    //FUNCIONES DE LOS BOTONES

    private void deleteProducto(String id) {
        String url = "http://192.168.0.6/invoicing/deleteProducto.php?id="+id;
        // requermiento del servidor a traves de una api por el metodo get, manda la informacion en formato jSON ingresa en on response
        StringRequest jrq = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.toString());
                Toast.makeText(getApplicationContext(), "Error al tratar de eliminar el producto: "+id, Toast.LENGTH_LONG).show();
            }
        });
        // hacer la peticion por el metdo GET
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(jrq); // manda a ejecutar la linea anterior
    }

    //FUNCION PARA BUSCAR

    private void searchProducto(String mreferencia) {

        String url = "http://192.168.0.6/invoicing/readProducto.php?referencia="+mreferencia;
        // requermiento del servidor a traves de una api por el metodo get, manda la informacion en formato jSON ingresa en on response
        JsonRequest jrq = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //customer mcustomer=new customer();

                JSONArray jsonArray=response.optJSONArray("producto");
                JSONObject jsonObject=null;

                try {
                    jsonObject = jsonArray.getJSONObject(0);//posición 0 del arreglo....
                    producto.setId(jsonObject.optString("id"));
                    producto.setNombre(jsonObject.optString("nombre"));
                    producto.setReferencia(jsonObject.optString("referencia"));
                    producto.setPrecio(jsonObject.optString("precio"));
                    producto.setStock(jsonObject.optString("stock"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                nombre.setText(producto.getNombre());//SE MODIFICA
                referencia.setText(producto.getReferencia());//SE MODIFICA
                precio.setText(producto.getPrecio());//SE MODIFICA
                stock.setText(producto.getStock());//SE MODIFICA
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error, no se encuentra la referencia del producto: "+mreferencia, Toast.LENGTH_LONG).show();
            }
        });
        // hacer la peticion por el metdo GET
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(jrq); // manda a ejecutar la linea anterior
    }

    //FUNCION ADD Y UPDATE

    private void addAndUpdateProducto(String id, String nombre, String referencia, String precio, String stock) {
        String url = "";
        if (id == null) {
            //guardar el cliente
            url = "http://192.168.0.6/invoicing/addProducto.php";
        } else {
            //actualizar el cliente
            url = "http://192.168.0.6/invoicing/updateProducto.php";
        }
        if(!nombre.isEmpty() && !referencia.isEmpty() && !precio.isEmpty() && !stock.isEmpty()){
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Registro de producto incorrecto!", Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<String, String>();
                    if (id != null) {
                        params.put("id", id);
                    }
                    params.put("nombre", nombre);
                    params.put("referencia", referencia);
                    params.put("precio", precio);
                    params.put("stock", stock);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);
        }
        else{
            Toast.makeText(getApplicationContext(), "Debe ingresar todos los datos", Toast.LENGTH_SHORT).show();
        }
    }

}