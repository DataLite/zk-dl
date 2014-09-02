package cz.datalite.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cz.datalite.concurrent.UniqueStamp;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test pro generovani unikatnich casovych znacek.
 * 
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public class UniqueStampTest {

	/**
	 * Pocet smycek, respektive pocet volani {@link cz.datalite.concurrent.UniqueStamp#getUniqueStamp()}.
	 */
	private static final int LOOP_COUNT = 2000;

	/**
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test
	public void testSingleUniqueStampTimeConsumption() throws InterruptedException, ExecutionException {

		List<Future<Long>> results = new ArrayList<>(LOOP_COUNT);

		// Pouzivame pool 10 vlaken
		ExecutorService exec = Executors.newFixedThreadPool(10);

		// Nalozime
		for (int i = 0; i < LOOP_COUNT; i++) {
			results.add(exec.submit(new UniqueStampRunnable()));
		}
		// Konec - uz nebudeme nakladat
		exec.shutdown();

		while (!exec.isTerminated()) {
			// Cekame, dokud vsechna vlakna neskonci
			Thread.sleep(1000);
		}

		List<Long> stamps = new ArrayList<>(results.size());
		for (Future<Long> future : results) {
			// System.out.println(future.get());
			stamps.add(future.get());
		}

		for (Iterator<Long> iterator = stamps.iterator(); iterator.hasNext();) {
			Long ts = iterator.next();
			iterator.remove();

			// Po odstraneni ts uz nesmi vysledky danou hodnotu obsahovat
			Assert.assertTrue(!stamps.contains(ts));
		}
	}

	/**
	 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
	 */
	protected static class UniqueStampRunnable
			implements Callable<Long> {

		private static UniqueStamp us = new UniqueStamp();

		@Override
		public Long call() throws Exception {
			return us.getUniqueStamp();
		}
	}

}
