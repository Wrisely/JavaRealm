package trial2dgame;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;


public class Sound {

	Clip clip;//to open audio file
	FloatControl volumeControl;
	URL soundURL[] = new URL[30];
	int volumeScale = 3;
	float volume;
	
	public Sound() {
		
		soundURL[0] = getClass().getResource("/sound/shoreBG.wav");
		soundURL[1] = getClass().getResource("/sound/coinSound.wav");
		soundURL[2] = getClass().getResource("/sound/buff.wav"); //teleport
		soundURL[3] = getClass().getResource("/sound/doorUnlock.wav");
		soundURL[4] = getClass().getResource("/sound/celebrate.wav"); //level up
		soundURL[5] = getClass().getResource("/sound/hitMonster.wav"); 
		soundURL[6] = getClass().getResource("/sound/receiveDamage.wav"); 
		soundURL[7] = getClass().getResource("/sound/clawSwing.wav"); //used for toggles
		soundURL[8] = getClass().getResource("/sound/forestBG.wav");
		soundURL[9] = getClass().getResource("/sound/libraryBG.wav");
		soundURL[10] = getClass().getResource("/sound/merchantBG.wav"); 
	//	soundURL[11] = getClass().getResource("/sound/.wav"); //missing battle
	//	soundURL[12] = getClass().getResource("/sound/.wav"); //missing gameOver


	}
	
	public void setFile(int i) {
		try {
			
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
			clip = AudioSystem.getClip();
			clip.open(ais);
			volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			checkVolume();
			
		}catch(Exception e) {	
			e.printStackTrace();
		}
	}
	
	public void setVolume(float value) {
		if (volumeControl != null) {
			volumeControl.setValue(value); // value in decibels (dB)
		}
	}
	
	public void play() {
		clip.start();
	}
	
	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);	
	}
	
	public void stop() {	
	    if (clip != null) {  // Check if clip is initialized
	        clip.stop();
	        clip.close();  // Close the clip when done
	    }
	}
	
	public void checkVolume() { //-80f - 6f
		
		switch(volumeScale) {
		case 0: volume = -80f; break;
		case 1: volume = -20f; break;
		case 2: volume = -12f; break;
		case 3: volume = -5f; break;
		case 4: volume = 1f; break;
		case 5: volume = 6f; break;
		}
		volumeControl.setValue(volume);
	}
	
	public void setVolumeByScaleAndAdjustment(int adjustment) {
		float baseVolume = 0.0f;
		switch(volumeScale) {
			case 0: baseVolume = -80f; break;
			case 1: baseVolume = -20f; break;
			case 2: baseVolume = -12f; break;
			case 3: baseVolume = -5f; break;
			case 4: baseVolume = 1f; break;
			case 5: baseVolume = 6f; break;
		}
		
		float finalVolume = baseVolume + adjustment;

		// Clamp the volume
		if (finalVolume > 6.0f) {
			finalVolume = 6.0f;
		}
		if (finalVolume < -80.0f) {
			finalVolume = -80.0f;
		}

		volumeControl.setValue(finalVolume);	
		}	
}
