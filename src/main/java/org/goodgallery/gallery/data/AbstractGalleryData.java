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

  protected AbstractGalleryData() {
    this.executor = Executors.newSingleThreadScheduledExecutor();
    this.autoSaveFuture = executor.schedule(this::save, 1, TimeUnit.SECONDS);
  }

  protected abstract void save();

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
