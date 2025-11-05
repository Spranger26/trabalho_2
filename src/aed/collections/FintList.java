package aed.collections;

import aed.utils.TemporalAnalysisUtils;

import java.util.Scanner;
import java.util.Iterator;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class FintList implements Iterable<Integer>
{
    private int[] data;
    private int[] data_dir;
    private int[] data_esq;
    private int index;
    private int size;
    private int head = -1;
    private int tail = -1;
    private static final int INITIAL_SIZE = 20;


    private class FinListIterator implements Iterator<Integer>
    {
        private int current;

        public FinListIterator()
        {
            this.current = FintList.this.head;
        }

        @Override
        public boolean hasNext()
        {
            return current != -1;
        }

        @Override
        public Integer next()
        {
            if (current == -1) throw new java.util.NoSuchElementException();
            int value = data[current];
            current = data_dir[current];
            return value;
        }
    }

    public FintList()
    {
        data = new int[INITIAL_SIZE];
        data_dir = new int[INITIAL_SIZE];
        data_esq = new int[INITIAL_SIZE];
        this.index = 0;
        this.size = 0;
    }

    private void resize()
    {
        // Se a lista estiver 75% cheia a gente da resize para o dobre.
        if (this.index >= this.data.length * 3 / 4)
        {
            int newSize = this.data.length * 2;
            data = java.util.Arrays.copyOf(data, newSize);
            data_dir = java.util.Arrays.copyOf(data_dir, newSize);
            data_esq = java.util.Arrays.copyOf(data_esq, newSize);
        }
    }

    public boolean add(int item)
    {
        resize();
        this.data[index] = item;
        data_esq[index] = tail;
        data_dir[index] = -1;

        if (tail != -1) data_dir[tail] = index;
        if (head == -1) head = index;

        tail = index;
        this.index++;
        this.size++;
        return true;
    }

    public int get()
    {
        if(isEmpty()) throw new IndexOutOfBoundsException("Falhou no Get First");

        return this.data[this.tail];
    }

    public boolean isEmpty()
    {
       return this.size == 0;
    }

    public int size()
    {
        return this.size;
    }

    public int remove()
    {
        if (isEmpty()) throw new IndexOutOfBoundsException("Falhou no remove");

        return removeAt(this.size - 1);
    }


    public void addAt(int index, int item)
    {
        if (index < 0 || index > this.size) throw new IndexOutOfBoundsException("Falhou no Add At");

        resize();
        int novo = this.index;
        this.data[novo] = item;

       if(index == 0)
        {  // Inserir no início — O(1)
            this.data[novo] = item;
            this.data_esq[novo] = -1;
            this.data_dir[novo] = head;

            if (head != -1) this.data_esq[head] = novo;
            head = novo;
            if (tail == -1) tail = novo;
        }
        else if (index == this.size)
        {
            // Inserir no fim — O(1)
            this.data_esq[novo] = tail;
            this.data_dir[novo] = -1;

            if (tail != -1) this.data_dir[tail] = novo;
            tail = novo;
        }
        else
        {
            // Procurar o anterior
            int temp;
            if (index < this.size / 2)
            {
                // Começar no início, mais rápido se index é pequeno
                temp = this.head;
                for (int i = 0; i < index - 1; i++) {
                    temp = this.data_dir[temp];
                }
            } else
            {
                // Começar no fim, mais rápido se index é grande
                temp = this.tail;
                for (int i = this.size - 1; i >= index; i--) {
                    temp = this.data_esq[temp];
                }
            }
            int prox = this.data_dir[temp];
            this.data_dir[temp] = novo;
            this.data_esq[novo] = temp;
            this.data_dir[novo] = prox;
            this.data_esq[prox] = novo;
        }
        this.index++;
        this.size++;
    }

    public int getFirst()
    {
        if(isEmpty()) throw new IndexOutOfBoundsException("Falhou no Get First");

        // Retorna o valor do elemento no índice físico 'head'
        return this.data[this.head];
    }

    public int get(int index)
    {
            if(index < 0 || index >= this.size) throw new IndexOutOfBoundsException("Falhou no Get");

        int current;

        if (index < this.size / 2)
        {
            current = this.head;
            for (int i = 0; i < index; i++)
            {
                current = this.data_dir[current];
            }
        }
        else
        {
            current = this.tail;
            for (int i = this.size - 1; i > index; i--)
            {
                current = this.data_esq[current];
            }
        }

        return this.data[current];
    }


    public void set(int index, int item)
    {
        if (index < 0 || index >= this.size) throw new IndexOutOfBoundsException("Falhou no set");
        int current;

        if (index < this.size / 2)
        {
            // Começar no head e avançar
            current = this.head;
            for (int i = 0; i < index; i++ )
            {
                current = this.data_dir[current];
            }
        }
        else
        {
            // Começar no tail e retroceder
            current = this.tail;
            for (int i = this.size - 1; i > index; i--)
            {
                current = this.data_esq[current];
            }
        }

        this.data[current] = item;
    }

    public int removeAt(int index)
    {
        if(index < 0 || index >= this.size) throw new IndexOutOfBoundsException("Falha no remove At");

        // encontrar o primeiro elemento da lista (data_esq == -1)
        int current;

        if (index < this.size / 2) {
            current = this.head;
            for(int i = 0; i < index; i++) {
                current = this.data_dir[current];
            }
        } else {
            current = this.tail;
            for(int i = this.size - 1; i > index; i--) {
                current = this.data_esq[current];
            }
        }

        int value = this.data[current];
        int anterior = this.data_esq[current];
        int next = this.data_dir[current];

        // Atualiza head e tail primeiro
        if(this.size == 1)
        {
            this.head = -1;
            this.tail = -1;
        }
        else if(index == 0)
        {
            this.head = next;
            this.data_esq[next] = -1;
        }
        else if(index == this.size - 1)
        {
            this.tail = anterior;
            this.data_dir[anterior] = -1;
        }
        else
        {
            this.data_dir[anterior] = next;
            this.data_esq[next] = anterior;
        }

        // Marca o nó como removido
        this.data_esq[current] = -5;
        this.data_dir[current] = -5;

        this.size--;
        return value;
    }

    public int indexOf(int item)
    {
        if (isEmpty()) return -1;               //lista vazia ?

        int pos = 0;          // posição lógica do elemento na lista
        int cur = this.head;       // começa no primeiro da lista
        while (cur != -1)            // -1 significa "não há mais nós"
        {
            if(this.data_esq[cur] != -5 && this.data_dir[cur] != -5)
            {
                if (this.data[cur] == item) {
                    return pos;
                }
                pos++;
                cur = this.data_dir[cur];
            }else
            {
                break;
            }
        }
        return -1;
    }

    public boolean contains(int item)
    {
        return this.indexOf(item) != -1;
    }


    public boolean remove(int item)
    {
        int pos = indexOf(item);
        if (pos == -1) return false;
        removeAt(pos);
        return true;
    }

    public void reverse()
    {
        if (this.size <= 1) return;             // impossivel inverter

        int current = this.head;

        // inverte todos os ponteiros da esq e dir
        int anterior = -1;
        while (current != -1) {
            int next = this.data_dir[current];

            // trocar as ligações
            this.data_dir[current] = anterior;
            this.data_esq[current] = next;

            anterior = current;
            current = next;
        }
        // Trocar head e a tail
        int oldHead = this.head;
        this.head = this.tail;
        this.tail = oldHead;
    }

    public FintList deepCopy()
    {
        FintList nova = new FintList();

        // garante que os arrays da nova lista têm pelo menos o tamanho de index
        int copyLength = Math.max(this.index, INITIAL_SIZE);

        nova.data = new int[copyLength];
        nova.data_dir = new int[copyLength];
        nova.data_esq = new int[copyLength];

        // copia todos os array
        System.arraycopy(this.data, 0, nova.data, 0, this.index);
        System.arraycopy(this.data_dir, 0, nova.data_dir, 0, this.index);
        System.arraycopy(this.data_esq, 0, nova.data_esq, 0, this.index);

        nova.index = this.index; // O próximo slot livre
        nova.size = this.size;
        nova.head = this.head;
        nova.tail = this.tail;

        return nova;
    }

    public Iterator<Integer> iterator()
    {
        return new FinListIterator();
    }

    //a utilizacao de ? super Integer e por causa da implementacao da interface Iterable
    public void forEach(Consumer<? super Integer> c)
    {
        int current = this.head;    // Começa no head
        while (current != -1)   // Segue a lista ligada
        {
            c.accept(this.data[current]);
            current = this.data_dir[current];
        }
    }

    public void map(UnaryOperator<Integer> op)
    {
        for (int i = 0; i < this.index; i++)
        {
            if (this.data_esq[i] == -5 || this.data_dir[i] == -5) continue;
            this.data[i] = op.apply(this.data[i]);
        }
    }

    public int reduce(BinaryOperator<Integer> op, int defaultValue)
    {
        int answer = defaultValue;
        int current = this.head;

        while (current != -1)
        {
            int item = this.data[current];
            answer = op.apply(answer, item);
            current = this.data_dir[current];
        }
        return answer;
    }

    public static void main(String[] args)
    {
        System.out.println("ADD AT MINHA LISTA");
        TemporalAnalysisUtils.runDoublingRatioTest(
                (Integer n) -> {
                    FintList list = new FintList();
                    for (int i = 0; i < n; i++) {
                        list.add(i);
                    }
                    return list;
                },
                (FintList list) -> {
                    for (int i = 0; i < list.size(); i += 5) {
                        list.addAt(i, i);
                    }
                },
                9);

        System.out.println("ADD AT LISTA PROF");
        TemporalAnalysisUtils.runDoublingRatioTest(
                (Integer n) -> {
                    LinkedList<Integer> list = new LinkedList<>();
                    for (int i = 0; i < n; i++) {
                        list.add(i);
                    }
                    return list;
                },
                (LinkedList<Integer> list) -> {
                    for (int i = 0; i < list.size(); i += 5) {
                        list.addAt(i, i);
                    }
                },
                9);

        System.out.println("REMOVE AT MINHA LISTA");
        TemporalAnalysisUtils.runDoublingRatioTest(
                (Integer n) -> {
                    FintList list = new FintList();
                    for (int i = 0; i < n; i++) {
                        list.add(i);
                    }
                    return list;
                },
                (FintList list) -> {
                    for (int i = list.size()-1; i >= 0; i -= 5) {
                        list.removeAt(i);
                    }
                },
                9);

        System.out.println("REMOVE AT LISTA PROF");
        TemporalAnalysisUtils.runDoublingRatioTest(
                (Integer n) -> {
                    LinkedList<Integer> list = new LinkedList<>();
                    for (int i = 0; i < n; i++) {
                        list.add(i);
                    }
                    return list;
                },
                (LinkedList<Integer> list) -> {
                    for (int i = list.size()-1; i >= 0; i -= 5) {
                        list.removeAt(i);
                    }
                },
                9);


        System.out.println("DEEPCOPY MINHA LISTA");
        TemporalAnalysisUtils.runDoublingRatioTest(
                (Integer n) -> {
                    FintList list = new FintList();
                    for (int i = 0; i < n; i++) {
                        list.add(i);
                    }
                    return list;
                },
                (FintList list) -> {
                    for (int i = 0; i < list.size(); i += 10) {
                        list.deepCopy();
                    }
                },
                9);

        System.out.println("SHALLOWCOPY LISTA PROF");
        TemporalAnalysisUtils.runDoublingRatioTest(
                (Integer n) -> {
                    LinkedList<Integer> list = new LinkedList<>();
                    for (int i = 0; i < n; i++) {
                        list.add(i);
                    }
                    return list;
                },
                (LinkedList<Integer> list) -> {
                    for (int i = 0; i < list.size(); i += 5) {
                        list.shallowCopy();
                    }
                },
                9);
        
    }
}

