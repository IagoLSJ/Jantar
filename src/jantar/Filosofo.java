package jantar;

public class Filosofo extends Thread {
    int id;
    //estados
    final int PENSANDO = 0;
    final int FAMINTO = 1;
    final int COMENDO = 2;
    
    public Filosofo(String nome, int id) {
        super(nome);
        this.id = id;
    }

    public void ComFome() {
        Principal.estado[this.id] = 1;
        System.out.println("O Filósofo " + getName() + " está FAMINTO!");
    }

    public void Come() {
        Principal.estado[this.id] = 2;
        System.out.println("O Filósofo " + getName() + " está COMENDO!");
        System.out.println("O Hashis está com " + getName());
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ex) {
            System.out.println("ERROR>" + ex.getMessage());
        }
    }

    public void Pensa() {
        Principal.estado[this.id] = 0;
        System.out.println("O Filósofo " + getName() + " está PENSANDO!");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ex) {
            System.out.println("ERROR>" + ex.getMessage());
        }
    }

    public void LargarHashis() throws InterruptedException {
        Principal.mutex.acquire();
        Pensa();
        Principal.filosofos[VizinhoEsquerda()].TentarObterHashis();
        Principal.filosofos[VizinhoDireita()].TentarObterHashis();
        Principal.mutex.release();
    }

    public void PegarHashis() throws InterruptedException {
        Principal.mutex.acquire();
        ComFome();
        TentarObterHashis();        
        Principal.mutex.release();
       
        Principal.semaforos[this.id].acquire();
    }

    public void TentarObterHashis() {

        if (Principal.estado[this.id] == 1
                && Principal.estado[VizinhoEsquerda()] != 2
                && Principal.estado[VizinhoDireita()] != 2) {
            Come();
            Principal.semaforos[this.id].release();
        } else {
            System.out.println(getName() + " não conseguiu comer!");
        }

    }

    @Override
    public void run() {
        try {
            Pensa();          
            do {
                PegarHashis();
                Thread.sleep(1000L);
                LargarHashis();
            } while (true);
        } catch (InterruptedException ex) {
            System.out.println("ERROR>" + ex.getMessage());
            return;
        }
    }

    public int VizinhoDireita() {
        return (this.id + 1) % 5;
    }

    public int VizinhoEsquerda() {
        if (this.id == 0) {            
            return 4;
        } else {
            return (this.id - 1) % 5;
        }
    }
}