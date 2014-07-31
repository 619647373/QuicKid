package com.example.quickid;

import java.util.HashSet;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TableRow;
import android.widget.TextView;

public class DialpadFragment extends Fragment implements View.OnClickListener,
		View.OnLongClickListener, View.OnKeyListener,
		AdapterView.OnItemClickListener, TextWatcher,
		PopupMenu.OnMenuItemClickListener, DialpadKeyButton.OnPressedListener {

	private View mDigitsContainer;
	private EditText mDigits;
	private ToneGenerator mToneGenerator;
	private final Object mToneGeneratorLock = new Object();
	private View mDelete;
	private View mDialpad;
	private final HashSet<View> mPressedDialpadKeys = new HashSet<View>(12);
	private boolean mDTMFToneEnabled;
	private static final int TONE_LENGTH_INFINITE = -1;
	private static final int TONE_RELATIVE_VOLUME = 80;
	private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_DTMF;

	public DialpadFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View fragmentView = inflater.inflate(R.layout.fragment_dialer,
				container, false);
		fragmentView.buildLayer();
		mDigits = (EditText) fragmentView.findViewById(R.id.digits);
		mDigits.setOnClickListener(this);
		mDigits.setOnKeyListener(this);
		mDigits.setOnLongClickListener(this);
		mDigits.addTextChangedListener(this);

		View oneButton = fragmentView.findViewById(R.id.one);
		if (oneButton != null) {
			setupKeypad(fragmentView);
		}

		mDelete = fragmentView.findViewById(R.id.deleteButton);
		if (mDelete != null) {
			mDelete.setOnClickListener(this);
			mDelete.setOnLongClickListener(this);
		}

		mDialpad = fragmentView.findViewById(R.id.dialpad); // This is null in
															// landscape mode.

		// In landscape we put the keyboard in phone mode.
		if (null == mDialpad) {
			mDigits.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
		} else {
			mDigits.setCursorVisible(false);
		}

		return fragmentView;
	}

	private void setupKeypad(View fragmentView) {
		final int[] buttonIds = new int[] { R.id.zero, R.id.one, R.id.two,
				R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven,
				R.id.eight, R.id.nine, R.id.star, R.id.pound };

		final int[] numberIds = new int[] { R.string.dialpad_0_number,
				R.string.dialpad_1_number, R.string.dialpad_2_number,
				R.string.dialpad_3_number, R.string.dialpad_4_number,
				R.string.dialpad_5_number, R.string.dialpad_6_number,
				R.string.dialpad_7_number, R.string.dialpad_8_number,
				R.string.dialpad_9_number, R.string.dialpad_star_number,
				R.string.dialpad_pound_number };

		final int[] letterIds = new int[] { R.string.dialpad_0_letters,
				R.string.dialpad_1_letters, R.string.dialpad_2_letters,
				R.string.dialpad_3_letters, R.string.dialpad_4_letters,
				R.string.dialpad_5_letters, R.string.dialpad_6_letters,
				R.string.dialpad_7_letters, R.string.dialpad_8_letters,
				R.string.dialpad_9_letters, R.string.dialpad_star_letters,
				R.string.dialpad_pound_letters };

		final Resources resources = getResources();

		DialpadKeyButton dialpadKey;
		TextView numberView;
		TextView lettersView;

		for (int i = 0; i < buttonIds.length; i++) {
			dialpadKey = (DialpadKeyButton) fragmentView
					.findViewById(buttonIds[i]);
			dialpadKey.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.MATCH_PARENT));
			dialpadKey.setOnPressedListener(this);
			numberView = (TextView) dialpadKey
					.findViewById(R.id.dialpad_key_number);
			lettersView = (TextView) dialpadKey
					.findViewById(R.id.dialpad_key_letters);
			final String numberString = resources.getString(numberIds[i]);
			numberView.setText(numberString);
			dialpadKey.setContentDescription(numberString);
			if (lettersView != null) {
				lettersView.setText(resources.getString(letterIds[i]));
			}
		}

		// Long-pressing one button will initiate Voicemail.
		fragmentView.findViewById(R.id.one).setOnLongClickListener(this);

		// Long-pressing zero button will enter '+' instead.
		fragmentView.findViewById(R.id.zero).setOnLongClickListener(this);

	}

	private void keyPressed(int keyCode) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
			playTone(ToneGenerator.TONE_DTMF_1, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_2:
			playTone(ToneGenerator.TONE_DTMF_2, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_3:
			playTone(ToneGenerator.TONE_DTMF_3, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_4:
			playTone(ToneGenerator.TONE_DTMF_4, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_5:
			playTone(ToneGenerator.TONE_DTMF_5, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_6:
			playTone(ToneGenerator.TONE_DTMF_6, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_7:
			playTone(ToneGenerator.TONE_DTMF_7, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_8:
			playTone(ToneGenerator.TONE_DTMF_8, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_9:
			playTone(ToneGenerator.TONE_DTMF_9, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_0:
			playTone(ToneGenerator.TONE_DTMF_0, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_POUND:
			playTone(ToneGenerator.TONE_DTMF_P, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_STAR:
			playTone(ToneGenerator.TONE_DTMF_S, TONE_LENGTH_INFINITE);
			break;
		default:
			break;
		}

		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
		mDigits.onKeyDown(keyCode, event);

		// If the cursor is at the end of the text we hide it.
		final int length = mDigits.length();
		if (length == mDigits.getSelectionStart()
				&& length == mDigits.getSelectionEnd()) {
			mDigits.setCursorVisible(false);
		}
	}

	@Override
	public void onPressed(View view, boolean pressed) {
		if (pressed) {
			switch (view.getId()) {
			case R.id.one: {
				keyPressed(KeyEvent.KEYCODE_1);
				break;
			}
			case R.id.two: {
				keyPressed(KeyEvent.KEYCODE_2);
				break;
			}
			case R.id.three: {
				keyPressed(KeyEvent.KEYCODE_3);
				break;
			}
			case R.id.four: {
				keyPressed(KeyEvent.KEYCODE_4);
				break;
			}
			case R.id.five: {
				keyPressed(KeyEvent.KEYCODE_5);
				break;
			}
			case R.id.six: {
				keyPressed(KeyEvent.KEYCODE_6);
				break;
			}
			case R.id.seven: {
				keyPressed(KeyEvent.KEYCODE_7);
				break;
			}
			case R.id.eight: {
				keyPressed(KeyEvent.KEYCODE_8);
				break;
			}
			case R.id.nine: {
				keyPressed(KeyEvent.KEYCODE_9);
				break;
			}
			case R.id.zero: {
				keyPressed(KeyEvent.KEYCODE_0);
				break;
			}
			case R.id.pound: {
				keyPressed(KeyEvent.KEYCODE_POUND);
				break;
			}
			case R.id.star: {
				keyPressed(KeyEvent.KEYCODE_STAR);
				break;
			}
			default: {
				break;
			}
			}
			mPressedDialpadKeys.add(view);
		} else {
			view.jumpDrawablesToCurrentState();
			mPressedDialpadKeys.remove(view);
			if (mPressedDialpadKeys.isEmpty()) {
				stopTone();
			}
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		final Editable digits = mDigits.getText();
        final int id = v.getId();
        switch (id) {
            case R.id.deleteButton: {
                digits.clear();
                // TODO: The framework forgets to clear the pressed
                // status of disabled button. Until this is fixed,
                // clear manually the pressed status. b/2133127
                mDelete.setPressed(false);
                return true;
            }
            case R.id.digits: {
                // Right now EditText does not show the "paste" option when cursor is not visible.
                // To show that, make the cursor visible, and return false, letting the EditText
                // show the option by itself.
                mDigits.setCursorVisible(true);
                return false;
            }
        }
        return false;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.deleteButton: {
				keyPressed(KeyEvent.KEYCODE_DEL);
				return;
			}
			case R.id.digits: {
				if (!isDigitsEmpty()) {
					mDigits.setCursorVisible(true);
				}
				return;
			}
			default: {
				return;
			}
		}
	}

	private boolean isDigitsEmpty() {
		return mDigits.length() == 0;
	}

	@Override
	public void onPause() {
		super.onPause();
		stopTone();
		mPressedDialpadKeys.clear();
	}

	@Override
	public void onResume() {
		super.onResume();
		mPressedDialpadKeys.clear();
		final ContentResolver contentResolver = getActivity()
				.getContentResolver();
		mDTMFToneEnabled = Settings.System.getInt(contentResolver,
				Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;
		synchronized (mToneGeneratorLock) {
			if (mToneGenerator == null) {
				try {
					mToneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE,
							TONE_RELATIVE_VOLUME);
				} catch (RuntimeException e) {
					mToneGenerator = null;
				}
			}
		}
	}

	private void playTone(int tone, int durationMs) {
		if (!mDTMFToneEnabled) {
			return;
		}
		AudioManager audioManager = (AudioManager) getActivity()
				.getSystemService(Context.AUDIO_SERVICE);
		int ringerMode = audioManager.getRingerMode();
		if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
				|| (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
			return;
		}

		synchronized (mToneGeneratorLock) {
			if (mToneGenerator == null) {
				return;
			}

			mToneGenerator.startTone(tone, durationMs);
		}
	}

	private void stopTone() {
		if (!mDTMFToneEnabled) {
			return;
		}
		synchronized (mToneGeneratorLock) {
			if (mToneGenerator == null) {
				return;
			}
			mToneGenerator.stopTone();
		}
	}
}
