package com.luizhrios.ucsalnotas;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity
{

    private Button signInButton_login;
    private Button signInButton_home;
    private Button signOutButton;
    Button forgotPasswordButton;

    private EditText mMatricula;
    private EditText mSenha;
    private TextView mNome;
    private SharedPreferences.Editor editor;
    private View login;
    private View home;
    String matricula;
    String senha;
    String nome;
    boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        login = getLayoutInflater().inflate(R.layout.activity_login, null);
        home = getLayoutInflater().inflate(R.layout.activity_home, null);
        SharedPreferences settings = getPreferences(0);
        editor = settings.edit();
        matricula = settings.getString("matricula", null);
        nome = settings.getString("nome", null);
        loggedIn = settings.getBoolean("loggedIn", false);
        if (loggedIn)
        {
            setContentView(home);
        } else
        {
            setContentView(login);
        }
        mMatricula = (EditText) login.findViewById(R.id.matricula);
        mSenha = (EditText) login.findViewById(R.id.senha);
        signInButton_login = (Button) login.findViewById(R.id.sign_in_button);
        mNome = (TextView) home.findViewById(R.id.nome);
        signInButton_home = (Button) home.findViewById(R.id.sign_in_button);
        signOutButton = (Button) home.findViewById(R.id.sign_out_button);
        forgotPasswordButton = (Button) login.findViewById(R.id.forgot_password_button);
        signInButton_login.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!mMatricula.getText().toString().equals("") && !mSenha.getText().toString().equals(""))
                {
                    matricula = new DecimalFormat("000000000").format(Double.parseDouble(mMatricula.getText().toString()));
                    senha = mSenha.getText().toString();
                    final AsyncNotas getNotas = new AsyncNotas();
                    signInButton_login.setEnabled(false);
                    forgotPasswordButton.setEnabled(false);
                    getNotas.execute(matricula, senha);
                } else if (mMatricula.getText().toString().equals("") && mSenha.getText().toString().equals(""))
                {
                    mMatricula.setError("Digite a matrícula");
                    mSenha.setError("Digite a senha");
                } else if (mMatricula.getText().toString().equals(""))
                {
                    mMatricula.setError("Digite a matrícula");
                } else if (mSenha.getText().toString().equals(""))
                {
                    mSenha.setError("Digite a senha");
                }
            }
        });
        mNome.setText("Olá, " + nome);
        signInButton_home.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final AsyncNotas getNotas = new AsyncNotas();
                signInButton_home.setEnabled(false);
                signOutButton.setEnabled(false);
                getNotas.execute(matricula);
            }
        });
        signOutButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                loggedIn = false;
                editor.remove("matricula");
                editor.remove("nome");
                editor.remove("loggedIn");
                editor.commit();
                setContentView(login);
            }
        });
        forgotPasswordButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!mMatricula.getText().toString().equals(""))
                {
                    matricula = new DecimalFormat("000000000").format(Double.parseDouble(mMatricula.getText().toString()));
                    final AsyncForgotPassword getNotas = new AsyncForgotPassword();
                    signInButton_login.setEnabled(false);
                    forgotPasswordButton.setEnabled(false);
                    getNotas.execute(matricula);
                } else
                {
                    mMatricula.setError("Digite a matrícula");
                }
            }
        });
    }

    enum ERROR
    {
        OK,
        NotFound,
        //        NoConnectionAvailable,
        NoConnection,
        PasswordIncorrect
    }

    class AsyncForgotPassword extends AsyncTask<String, String, String>
    {
        HttpURLConnection client;

        protected String doInBackground(String... params)
        {
            String[] tokens = validationTokens();
            String result = sendRequest(params[0], tokens);
            return result;
        }

        protected void onPostExecute(String result)
        {
            Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
            signInButton_login.setEnabled(true);
            forgotPasswordButton.setEnabled(true);
        }

        private String sendRequest(String matricula, String[] tokens)
        {
            try
            {
                URL url = new URL("http://www4.ucsal.br/trocasenha/esqueciminhasenha.aspx");
                try
                {
                    byte[] params = ("__VIEWSTATE=" + tokens[0] + "&__EVENTVALIDATION=" + tokens[1] + "&txtEmail=" + matricula + "&BtnContinuar=ENVIAR").getBytes();
                    String result = "";
                    client = (HttpURLConnection) url.openConnection();
                    client.setDoOutput(true);
                    client.setDoInput(true);
                    OutputStream writer = client.getOutputStream();
                    writer.write(params);
                    writer.close();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null)
                        result += line + '\n';
                    reader.close();
                    Matcher LblMensagem = Pattern.compile("<span id=\"LblMensagem\">(.+)<\\/span>").matcher(result);
                    LblMensagem.find();
                    return LblMensagem.group(1);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        private String[] validationTokens()
        {
            try
            {
                URL url = new URL("http://www4.ucsal.br/trocasenha/esqueciminhasenha.aspx");
                try
                {
                    String result = "";
                    client = (HttpURLConnection) url.openConnection();
                    client.setDoInput(true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null)
                        result += line + '\n';
                    reader.close();
                    Matcher viewState = Pattern.compile("<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"(.+)\" \\/>").matcher(result);
                    Matcher eventValidation = Pattern.compile("<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"(.+)\" \\/>").matcher(result);
                    viewState.find();
                    eventValidation.find();
                    return new String[]{URLEncoder.encode(viewState.group(1)), URLEncoder.encode(eventValidation.group(1))};
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    class AsyncNotas extends AsyncTask<String, String, Bundle>
    {
        HttpURLConnection client;
        final int TIMEOUT = 10000;

        protected Bundle doInBackground(String... params)
        {
            Bundle data = new Bundle();
            ArrayList<Subject> Subjects = new ArrayList<>();
            Subject subject;
            String tablenotas;
            try
            {
                if (loggedIn || verifyPassword(params[0], params[1]))
                {
                    nome = getNome(params[0]);
                    tablenotas = getNotas(params[0]);
                    if (tablenotas != null && nome != null)
                    {
                        data.putString("nome", nome);
                        Matcher nomematcher = Pattern.compile("(\\w)(\\w+)").matcher(nome);
                        nomematcher.find();
                        nome = nomematcher.group(1) + nomematcher.group(2).toLowerCase();
                        nomematcher.find();
                        nome += " " + nomematcher.group(1) + nomematcher.group(2).toLowerCase();
                        editor.putString("matricula", params[0]);
                        editor.putString("nome", nome);
                        loggedIn = true;
                        editor.putBoolean("loggedIn", true);
                        editor.commit();
                        Matcher subjects = Pattern.compile("(\\(\\d{5}\\).+)<").matcher(tablenotas);
                        Matcher notas = Pattern.compile(">(\\d{1,2}\\.\\d{1,2}|\\d{1,2})<").matcher(tablenotas);
                        while (subjects.find())
                        {
                            subject = new Subject();
                            subject.Nome = subjects.group(1);
                            notas.find();
                            if (!notas.group(1).equals("00.0"))
                                subject.AV1 = Double.parseDouble(notas.group(1));
                            notas.find();
                            if (!notas.group(1).equals("00.0"))
                                subject.AV2 = Double.parseDouble(notas.group(1));
                            notas.find();
                            if (!notas.group(1).equals("00.0"))
                                subject.AVI = Double.parseDouble(notas.group(1));
                            notas.find();
                            subject.Faltas = Integer.parseInt(notas.group(1));
                            notas.find();
                            if (subject.AV1 != null)
                                subject.MP = Double.parseDouble(notas.group(1));
                            notas.find();
                            if (!notas.group(1).equals("00.0"))
                                subject.MF = Double.parseDouble(notas.group(1));
                            Subjects.add(subject);
                        }
                        data.putSerializable("Subjects", Subjects);
                    } else
                    {
                        data.putSerializable("Status", ERROR.NoConnection);
                        return data;
                    }
                    if (Subjects.size() > 0)
                        data.putSerializable("Status", ERROR.OK);
                    else
                        data.putSerializable("Status", ERROR.NotFound);
                } else
                    data.putSerializable("Status", ERROR.PasswordIncorrect);
            } catch (IOException e)
            {
                data.putSerializable("Status", ERROR.NoConnection);
                return data;
            }
            return data;
        }

        private String getNome(String matricula) throws IOException
        {
            URL url = new URL("http://portal.ucsal.br/nomeAlunoG.php");
            byte[] params = ("matricula=" + matricula).getBytes();
            String result;
            client = (HttpURLConnection) url.openConnection();
            client.setReadTimeout(TIMEOUT);
            client.setDoOutput(true);
            client.setDoInput(true);
            OutputStream writer = client.getOutputStream();
            writer.write(params);
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            result = reader.readLine();
            reader.close();
            return result;
        }

        private String getNotas(String matricula) throws IOException
        {
            URL url = new URL("http://portal.ucsal.br/notasAlunoG.php");
            byte[] params = ("nu_matricula=" + matricula).getBytes();
            String result = "";
            client = (HttpURLConnection) url.openConnection();
            client.setReadTimeout(TIMEOUT);
            client.setDoOutput(true);
            client.setDoInput(true);
            OutputStream writer = client.getOutputStream();
            writer.write(params);
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
                result += line + '\n';
            reader.close();
            return result.replace("<td rowspan=2></td>", "<td rowspan=2>00.0</td>");
        }

        private boolean verifyPassword(String matricula, String senha) throws IOException
        {
            URL url = new URL("http://portal.ucsal.br/tq-loging.php");
            byte[] params = ("userg=" + matricula + "&passg=" + senha).getBytes();
            client = (HttpURLConnection) url.openConnection();
            client.setReadTimeout(TIMEOUT);
            client.setDoOutput(true);
            client.setInstanceFollowRedirects(false);
            OutputStream writer = client.getOutputStream();
            writer.write(params);
            writer.close();
            return client.getResponseCode() == 200;
        }

        protected void onPostExecute(Bundle data)
        {
            switch ((ERROR) data.getSerializable("Status"))
            {
                case OK:
                    mNome.setText("Olá, " + nome);
                    Intent intent = new Intent(LoginActivity.this, Notas.class);
                    intent.putExtras(data);
                    startActivity(intent);
                    setContentView(home);
                    break;
                case NotFound:
                    Toast.makeText(LoginActivity.this, "Matrícula não encontrada", Toast.LENGTH_LONG).show();
                    break;
//                case NoConnectionAvailable:
//                    Toast.makeText(LoginActivity.this, "Sem internet disponível", Toast.LENGTH_LONG).show();
//                    break;
                case NoConnection:
                    Toast.makeText(LoginActivity.this, "Sem conexão", Toast.LENGTH_LONG).show();
                    break;
                case PasswordIncorrect:
                    Toast.makeText(LoginActivity.this, "Senha Incorreta", Toast.LENGTH_LONG).show();
                    break;
            }
            signInButton_login.setEnabled(true);
            forgotPasswordButton.setEnabled(true);
            signInButton_home.setEnabled(true);
            signOutButton.setEnabled(true);
        }
    }
}