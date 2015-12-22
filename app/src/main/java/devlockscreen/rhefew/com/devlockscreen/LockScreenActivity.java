package devlockscreen.rhefew.com.devlockscreen;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

public class LockScreenActivity extends Activity implements
        LockscreenUtils.OnLockStatusChangedListener {

    // User-interface
    private ImageView imgRandomSkill;
    private ImageView imgSlot1;
    private ImageView imgSlot2;
    private ImageView imgSlot3;

    // Member variables
    private LockscreenUtils mLockscreenUtils;
    private Skill skill = new Skill();
    private Skill randomSkill = new Skill();
    private int attempts = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lockscreen);

        imgRandomSkill = (ImageView)findViewById(R.id.imgSkill);
        imgSlot1 = (ImageView)findViewById(R.id.imgSlot1);
        imgSlot2 = (ImageView)findViewById(R.id.imgSlot2);
        imgSlot3 = (ImageView)findViewById(R.id.imgSlot3);

        resetSkillAttributes(true);

        init();

        // unlock screen in case of app get killed by system
        if (getIntent() != null && getIntent().hasExtra("kill")
                && getIntent().getExtras().getInt("kill") == 1) {
            enableKeyguard();
            unlockHomeButton();
        } else {

            try {
                // disable keyguard
                disableKeyguard();

                // lock home button
                lockHomeButton();

                // start service for observing intents
                startService(new Intent(this, LockscreenService.class));

                // listen the events get fired during the call
                StateListener phoneStateListener = new StateListener();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                telephonyManager.listen(phoneStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE);

            } catch (Exception e) {}

        }
    }

    private void resetSkillAttributes(boolean resetSkill) {

        if(resetSkill) {
            randomSkill = new Skill().createRandomSkill();
            imgRandomSkill.setImageResource(getResources().getIdentifier(randomSkill.getName().toString().toLowerCase(), "drawable", getPackageName()));
        }

        skill.setQuas(0);
        skill.setWex(0);
        skill.setExort(0);
        skill.setSkillPoints(0);

        imgSlot1.setImageDrawable(null);
        imgSlot2.setImageDrawable(null);
        imgSlot3.setImageDrawable(null);

    }

    public void putSkillPoint(View view){
        if(skill.getSkillPoints()<3) {

            String skillName = view.getContentDescription().toString();
            if (skillName.equals("quas")) {
                skill.setQuas(skill.getQuas() + 1);
            } else if (skillName.equals("wex")) {
                skill.setWex(skill.getWex() + 1);
            } else if (skillName.equals("exort")) {
                skill.setExort(skill.getExort() + 1);
            }

            int resource = getResources().getIdentifier(skillName + "_round", "drawable", getPackageName());
            if (skill.getSkillPoints() == 1) {
                imgSlot1.setImageResource(resource);
            } else if (skill.getSkillPoints() == 2) {
                imgSlot2.setImageResource(resource);
            } else if (skill.getSkillPoints() == 3) {
                imgSlot3.setImageResource(resource);
            }
        }
    }

    private void init() {
        mLockscreenUtils = new LockscreenUtils();
        ImageView imgInvoke = (ImageView)findViewById(R.id.imgInvoke);
        imgInvoke.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (attempts < 3 || randomSkill.compareWith(skill)) {
                    // unlock home button and then screen on button press
                    if(randomSkill.compareWith(skill)){
                        unlockHomeButton();
                    } else {
                        attempts++;
                        resetSkillAttributes(false);
                        Vibrator a  = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        a.vibrate(100);
                    }
                }else{
                    attempts=1;
                    resetSkillAttributes(true);
                }
            }
        });

    }

    // Handle events of calls and unlock screen if necessary
    private class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    unlockHomeButton();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    };

    // Don't finish Activity on Back press
    @Override
    public void onBackPressed() {
        return;
    }

    // Handle button clicks
    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (keyCode == KeyEvent.KEYCODE_POWER)
                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {

            return true;
        }

        return false;

    }

    // handle the key press events here itself
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            return false;
        }
        if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {

            return true;
        }
        return false;
    }

    // Lock home button
    public void lockHomeButton() {
        mLockscreenUtils.lock(LockScreenActivity.this);
    }

    // Unlock home button and wait for its callback
    public void unlockHomeButton() {
        mLockscreenUtils.unlock();
    }

    // Simply unlock device when home button is successfully unlocked
    @Override
    public void onLockStatusChanged(boolean isLocked) {
        if (!isLocked) {
            unlockDevice();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unlockHomeButton();
    }

    @SuppressWarnings("deprecation")
    private void disableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.disableKeyguard();
    }

    @SuppressWarnings("deprecation")
    private void enableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.reenableKeyguard();
    }

    //Simply unlock device by finishing the activity
    private void unlockDevice()
    {
        finish();
    }

}