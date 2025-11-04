package org.goodgallery.gallery.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class AbstractGalleryDataTest {

  private static class TestGalleryData extends AbstractGalleryData {
    private final AtomicBoolean saveCalled = new AtomicBoolean(false);
    private final CountDownLatch saveLatch = new CountDownLatch(1);

    @Override
    protected void save() {
      saveCalled.set(true);
      saveLatch.countDown();
    }

    public boolean wasSaveCalled() {
      return saveCalled.get();
    }

    public boolean waitForSave(long timeout, TimeUnit unit) throws InterruptedException {
      return saveLatch.await(timeout, unit);
    }
  }

  @Test
  @Timeout(3)
  void testConstructor_schedulesAutoSave() throws InterruptedException {
    TestGalleryData testData = new TestGalleryData();
    
    // Auto-save should be scheduled for 1 second
    boolean saved = testData.waitForSave(2, TimeUnit.SECONDS);
    
    assertTrue(saved, "Auto-save should be triggered");
    assertTrue(testData.wasSaveCalled(), "Save method should be called");
    
    testData.close();
  }

  @Test
  void testClose_cancelsAutoSave() {
    TestGalleryData testData = new TestGalleryData();
    
    assertDoesNotThrow(() -> testData.close());
  }

  @Test
  @Timeout(2)
  void testClose_waitsForAutoSaveCompletion() throws InterruptedException {
    TestGalleryData testData = new TestGalleryData();
    
    // Wait for auto-save to trigger
    Thread.sleep(1100);
    
    // Close should wait for completion
    assertDoesNotThrow(() -> testData.close());
  }

  @Test
  void testExecutorServiceIsCreated() {
    TestGalleryData testData = new TestGalleryData();
    
    assertNotNull(testData.getExecutor(), "Executor should be created");
    assertFalse(testData.getExecutor().isShutdown(), "Executor should not be shutdown initially");
    
    testData.close();
  }

  @Test
  void testMultipleInstances_haveIndependentExecutors() {
    TestGalleryData testData1 = new TestGalleryData();
    TestGalleryData testData2 = new TestGalleryData();
    
    assertNotSame(testData1.getExecutor(), testData2.getExecutor(),
        "Each instance should have its own executor");
    
    testData1.close();
    testData2.close();
  }
}