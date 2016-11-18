package me.piruin.phototaker;

public interface PhotoTakerListener {

  void onCancel(int action);

  void onError(int action);
}
