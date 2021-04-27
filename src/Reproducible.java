/*
 * Reproducible
 * This interface defines the methods that must be overriden by the class of an object type that is able to reproduce
 */
interface Reproducible {
  public boolean canBreed();
  public boolean getIsFemale();
  public void decreaseHealthOnBreeding();
}