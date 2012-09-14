import org.gearman.*;

class Main { public static void main(String[] args) {

  /*Gearman gearmanClient = Gearman.createGearman();
  Gearman gearmanWorker = Gearman.createGearman();*/

  Gearman gearman = Gearman.createGearman();
  GearmanClient client = gearman.createGearmanClient();
  GearmanWorker worker = gearman.createGearmanWorker();

  try {
    client.addServer(gearman.createGearmanServer("localhost", 4730));
    worker.addServer(gearman.createGearmanServer("localhost", 4730));

    worker.addFunction("test_echo", new GearmanFunction() {
      public byte[] work(
        String funcName, byte[] data, GearmanFunctionCallback cb
      ) {
        String inputStr = new String(data);
        return new StringBuffer(inputStr).reverse().toString().getBytes();
      }
    });

    GearmanJobReturn gearmanRet = client.submitJob("test_echo", "hello, world".getBytes());
    while(!gearmanRet.isEOF()) {
      try {
        byte[] data = gearmanRet.poll().getData();
        System.out.println(new String(data));
      } catch(InterruptedException ex) { }
    }
  } finally {
    gearman.shutdown();
  }

  System.out.println("I should exit after this.");
}}
