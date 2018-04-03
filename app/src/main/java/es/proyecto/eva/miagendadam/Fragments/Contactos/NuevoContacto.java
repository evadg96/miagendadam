package es.proyecto.eva.miagendadam.Fragments.Contactos;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

public class NuevoContacto extends AppCompatActivity {
    private String url_consulta = "http://miagendafp.000webhostapp.com/inserta_nuevo_contacto.php";
    EditText txtNombre, txtCorreo, txtModulo, txtTelefono;
    private String nombreContacto = "", correoContacto = "", modulo = "", telefono = "", idUsuario = "";
    private StringRequest request;
    private String pattern_formato_nombre = "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|ñ|o|p|q|r|s|t|u|v|w|x|y|z" // minúsculas
            + "|A|B|C|D|E|F|G|H|I|J|K|L|M|N|Ñ|O|P|Q|R|S|T|U|V|W|X|Y|Z" // mayúsculas
            + "|á|é|í|ó|ú|Á|É|Í|Ó|Ú|ç|Ç|à|è|ì|ò|ù|À|È|Ì|Ò|Ù|ä|ë|ï|ö|ü|Ä|Ë|Ï|Ö|Ü|â|ê|î|ô|û|Â|Ê|Î|Ô|Û|ã|õ|Ã|Õ)+"; // letras con tildes u otros caracteres
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_contacto);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Nuevo contacto");
        txtNombre = (EditText) findViewById(R.id.txt_nombre_contacto);
        txtCorreo = (EditText) findViewById(R.id.txt_correo);
        txtModulo = (EditText) findViewById(R.id.txt_modulo);
        txtTelefono = (EditText) findViewById(R.id.txt_telefono);
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", ""); // obtenemos el id del usuario al que vamos a introducir el registro.
    }

    // Añade los iconos a la barra de acciones (en este caso, el de guardar)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo, menu);
        return true; // .menu es el directorio, y .menu_nuevo el archivo
    }

    /***********************************************************************************************
     *     Opciones del menú de la barra de acciones
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.menu_guardar: // Opción de guardar registro
                // Log.i("NuevoRegistroDiario", "Action Guardar registro");
                nombreContacto = txtNombre.getText().toString();
                correoContacto = txtCorreo.getText().toString();
                modulo = txtModulo.getText().toString();
                telefono = txtTelefono.getText().toString();
                // Todos los campos son obligatorios salvo el del teléfono, que no se tiene por qué conocer
                if (nombreContacto.isEmpty() || correoContacto.isEmpty() || modulo.isEmpty()){
                    // Toast.makeText(NuevoRegistroDiario.this, "Debes completar todos los datos.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(NuevoContacto.this, R.string.error_campos_vacios, Toast.LENGTH_LONG).show();
                } else { // pasamos a validación de nombre
                    Pattern pattern = Pattern.compile(pattern_formato_nombre); // creamos el patrón asignándole los caracteres que no queremos que tenga
                    Matcher matcher = pattern.matcher(nombreContacto); // le indicamos que queremos que aplique el patrón al correo
                    if (!matcher.matches()) { // si el nombre del contacto a guardar no contiene exclusivamente caracteres del patrón, no dejamos guardar
                        Toast.makeText(NuevoContacto.this, R.string.error_nombre_invalido, Toast.LENGTH_SHORT).show();
                    } else {
                        guardarContacto();
                    }
                }
                return true;
            case android.R.id.home: // Opción de volver hacia atrás
                // Log.i("NuevoRegistroDiario", "Action Atrás");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     * Método que guarda el nuevo contacto introducido
     **********************************************************************************************/
    public void guardarContacto(){
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("1")){
                            Toast.makeText(NuevoContacto.this, R.string.contacto_guardado, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(NuevoContacto.this, R.string.error_guardar_contacto, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NuevoContacto.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombreContacto", nombreContacto);
                parametros.put("correoContacto", correoContacto);
                parametros.put("telefono", telefono);
                parametros.put("modulo", modulo);
                parametros.put("idUsuario", idUsuario); // pasamos el nombre de usuario como parámetro de la consulta para obtener sus registros del diario
                return parametros;
            }

        };
        AppController.getInstance().addToRequestQueue(request);
    }
}