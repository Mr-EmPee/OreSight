package ml.empee.oresight.model.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ml.empee.oresight.model.content.Sight;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.time.LocalDateTime;

/**
 * Events that is fired when a sight effect is given
 */

@Getter
@RequiredArgsConstructor(staticName = "of")
public class SightEffectStartEvent extends Event {

  private static final HandlerList HANDLERS = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  private final Player player;
  private final Sight sight;
  private final LocalDateTime expireTime;

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }
}
