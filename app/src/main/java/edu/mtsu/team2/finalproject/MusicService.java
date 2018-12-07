package edu.mtsu.team2.finalproject;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Random;

import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;


public class MusicService extends Activity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, View.OnClickListener{
    //Media Player, Song List, and Song Position
    private MediaPlayer music;
    private ArrayList<Song> songs;
    private int songPos;
    private boolean shuffle=false;
    private Random rand;
    private Button play;
    private Button back;
    private Button next;

    //inner binder class
    private final IBinder musicBind = new MusicBinder();

    /*@Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources
    @Override
    public boolean onUnbind(Intent intent) {
        music.stop();
        music.release();
        return false;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songPos = 0; // starting position at 0
        music = new MediaPlayer(); // creating media player
        setMusicPlayer();
        setContentView(R.layout.mediaplayer);

        play = (Button) findViewById(R.id.play);
        next = (Button) findViewById(R.id.next);
        back = (Button) findViewById(R.id.back);

        songs = SongListActivity.songList.getSongs();
        play.setOnClickListener(this);

    }

    //initializing media player
    public void setMusicPlayer(){
        //lets music keep playing
        music.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        //stream type music
        music.setAudioStreamType(AudioManager.STREAM_MUSIC);
        music.setOnPreparedListener(this); //when instance is made
        music.setOnCompletionListener(this); //when song has finished
        music.setOnErrorListener(this); //when there is a error
    }

    // getting songs from main activity
    public void getSongs(ArrayList<Song> theSongs){
        songs = theSongs;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                playSong();
        }
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
       //start the playback
        mp.start();
    }

    //setting song
    public void setSong(int songI){
        songPos=songI;
    }

    //When playing song
    public void playSong() {
        //play.setOnClickListener(this);
        music.reset();
        Song song = songs.get(songPos); //getting song
        long currSong = song.getId(); //getting id
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong); //set uri
        //setting datasource but checking for error
        try {
            music.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
            music.prepareAsync();
        }
    }
        public int getPosn(){
            return music.getCurrentPosition();
        }

        public int getDur(){
            return music.getDuration();
        }

        public boolean isPng(){
            return music.isPlaying();
        }

        public void pausePlayer(){
            music.pause();
        }

        public void seek(int posn){
            music.seekTo(posn);
        }

        public void go(){
            music.start();
        }

    public void playPrev(){
        back.setOnClickListener(this);
        songPos--;
        if(songPos < 0){ songPos=songs.size()-1;};
        playSong();
    }

    //skip to next
    public void playNext(){
        next.setOnClickListener(this);
        songPos++;
        if(songPos >= songs.size()) {songPos=0;};
        playSong();

        if(shuffle){
            int newSong = songPos;
            while(newSong==songPos){
                newSong=rand.nextInt(songs.size());
            }
            songPos=newSong;
        }
        else{
            songPos++;
            if(songPos >=songs.size()) songPos=0;
        }
        playSong();
    }












}
