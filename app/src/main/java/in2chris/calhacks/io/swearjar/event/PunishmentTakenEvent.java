package in2chris.calhacks.io.swearjar.event;

/**
 * Created by ckroetsc on 10/5/14.
 */
public class PunishmentTakenEvent {

  private Type mType;

  public enum Type {
    DONATION, FB_SHAMING;
  }

  public PunishmentTakenEvent(Type type) {
    mType = type;
  }

  public Type getType() {
    return mType;
  }
}
