package aqua.blatt1.broker;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import aqua.blatt1.common.Direction;
import aqua.blatt1.common.msgtypes.DeregisterRequest;
import aqua.blatt1.common.msgtypes.HandoffRequest;
import aqua.blatt1.common.msgtypes.RegisterRequest;
import aqua.blatt1.common.msgtypes.RegisterResponse;
import aqua.blatt1.common.msgtypes.NeighborUpdate;
import aqua.blatt2.broker.PoisonPill;
import messaging.Endpoint;
import messaging.Message;

public class Broker {

	static int i = 1;

	static Endpoint endpoint = new Endpoint(4711);

	static ClientCollection<InetSocketAddress> list = new ClientCollection<>();
	static int NUM_THREADS = 5; // beispiel;
	static ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
	static ReadWriteLock lock = new ReentrantReadWriteLock();

	static volatile boolean poisoner = false;

	public static void broker() {

		while (!poisoner) {

			Message message = endpoint.blockingReceive();
			executor.execute(new BrokerTask(message));
		}
	}

	public static void register(InetSocketAddress sender) {

		lock.writeLock().lock();
		String id = "Aquarium " + i;
		i++;

		list.add(id, sender);

		endpoint.send(sender, new RegisterResponse(id));
		lock.writeLock().unlock();

		NeighborUpdate update = new NeighborUpdate(sender);
		InetSocketAddress neighborLeft = list.getLeftNeighorOf(list.indexOf(sender));
		endpoint.send(neighborLeft, update);

		InetSocketAddress neighborRight = list.getRightNeighorOf(list.indexOf(sender));
		endpoint.send(neighborRight, update);

	}

	public static void handoff(InetSocketAddress sender, HandoffRequest handoff) {

		lock.readLock().lock();
		InetSocketAddress temp;

		int index = list.indexOf(sender);
		if (handoff.getFish().getDirection().equals(Direction.LEFT)) {
			temp = list.getLeftNeighorOf(index);
		} else {
			temp = list.getRightNeighorOf(index);
		}
		endpoint.send(temp, handoff);
		lock.readLock().unlock();

	}

	public static void deregister(DeregisterRequest deregisterRequest) {

		lock.writeLock().lock();

		int index = list.indexOf(deregisterRequest.getId());

		if (index != -1) {
			list.remove(index);
		}

		lock.writeLock().unlock();

	}

	public static void main(String args[]) {
		broker();
		executor.shutdown();
	}

	public static class BrokerTask implements Runnable {

		private Message message;

		public BrokerTask(Message message) {
			this.message = message;
		}

		public void test() {

			if (message.getPayload() instanceof RegisterRequest) {

				register(message.getSender());

			} else if (message.getPayload() instanceof HandoffRequest) {

				handoff(message.getSender(), (HandoffRequest) message.getPayload());

			} else if (message.getPayload() instanceof DeregisterRequest) {

				deregister((DeregisterRequest) message.getPayload());

			} else if (message.getPayload() instanceof PoisonPill) {
				poisoner = true;

			}

		}

		@Override
		public void run() {
			test();
		}

	}
}
