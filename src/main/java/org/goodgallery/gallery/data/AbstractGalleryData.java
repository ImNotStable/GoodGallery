package org.goodgallery.gallery.data;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractGalleryData implements GalleryData, Closeable {

  @Getter(AccessLevel.PROTECTED)
  private final ScheduledExecutorService executor;
  private final ScheduledFuture<?> autoSaveFuture;

  /**
   * Initializes a single-thread {@link java.util.concurrent.ScheduledExecutorService} and
   * schedules a one-time execution of {@link #save()} to run after 1 second; the resulting
   * {@code ScheduledFuture} is stored in {@code autoSaveFuture}.
   */
  protected AbstractGalleryData() {
    this.executor = Executors.newSingleThreadScheduledExecutor();
    this.autoSaveFuture = executor.schedule(this::save, 1, TimeUnit.SECONDS);
  }

  /**
 * Persist the gallery's current state to permanent storage.
 *
 * Implementations should perform any necessary synchronization and ensure the saved state is durable.
 */
protected abstract void save();

  /**
   * Cancels the scheduled auto-save task and waits for that task to complete.
   *
   * <p>The method requests cancellation of the internal scheduled save and blocks until the
   * scheduled future reports completion.</p>
   *
   * @throws RuntimeException if the waiting thread is interrupted while awaiting task completion
   */
  public void close() {
    autoSaveFuture.cancel(false);

    try {
      while (!autoSaveFuture.isDone())
        wait(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}