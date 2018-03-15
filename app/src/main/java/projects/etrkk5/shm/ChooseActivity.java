package projects.etrkk5.shm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener {
    Button buttonCompany;
    Button buttonEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        buttonCompany = (Button)findViewById(R.id.buttonCompany);
        buttonEmployee = (Button)findViewById(R.id.buttonEmployee);

        buttonCompany.setOnClickListener(this);
        buttonEmployee.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == buttonCompany.getId()){
            startActivity(new Intent(ChooseActivity.this, SignUpCompanyActivity.class));
        }

        if(v.getId() == buttonEmployee.getId()){
            startActivity(new Intent(ChooseActivity.this, SignUpEmployeeActivity.class));
        }

    }
}
