package ml.empee.oresight.model.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ml.empee.oresight.model.content.Sight;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Events that is fired when a sight effect is given
 */

@Getter
@RequiredArgsConstructor(staticName = "of")
public class SightEffectEndEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  private final UUID player;
  private final Sight sight;
  private final LocalDateTime expireTime;

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }
}
