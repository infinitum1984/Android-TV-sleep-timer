package com.infnitum.sleeptimer
import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast


class MainActivity : AppCompatActivity(){

    lateinit var button_60: Button
    lateinit var button_30: Button

    val NAME_VAL_TIME="time_to_count"

    val RESULT_ENABLE = 11

    lateinit var devicePolicyManager: DevicePolicyManager
    lateinit var activityManager: ActivityManager
    lateinit var compName: ComponentName


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_30 = findViewById(R.id.min_30)
        button_60=findViewById(R.id.min_60)


        try {
            devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            compName = ComponentName(this,MyAdmin::class.java)


            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission")
            startActivityForResult(intent, RESULT_ENABLE)

        }catch (e:Exception){
            Toast.makeText(this@MainActivity, "Exception get admin permissions", Toast.LENGTH_SHORT).show()

        }




        val onClickListener:View.OnClickListener= View.OnClickListener {
            if (it.id==button_30.id){
                val intent = Intent(applicationContext,TimerActivity::class.java);
                intent.putExtra(NAME_VAL_TIME,1800000L)
                startActivity(intent)
            }
            if(it.id==button_60.id){
                val intent = Intent(applicationContext,TimerActivity::class.java);
                intent.putExtra(NAME_VAL_TIME,3600000L)
                startActivity(intent)            }
        }
        button_30.setOnClickListener(onClickListener)
        button_60.setOnClickListener(onClickListener)

        findViewById<Button>(R.id.test).setOnClickListener {
            val intent = Intent(applicationContext,TimerActivity::class.java);
            intent.putExtra(NAME_VAL_TIME,60000L)
            startActivity(intent)            }
        findViewById<Button>(R.id.disable_admin).setOnClickListener {
            devicePolicyManager.removeActiveAdmin(compName)
        }
    }

    override fun onResume() {
        super.onResume()
        devicePolicyManager.isAdminActive(compName)

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RESULT_ENABLE -> if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this@MainActivity, "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }




}




