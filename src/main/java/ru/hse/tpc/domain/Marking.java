package ru.hse.tpc.domain;

public class Marking {

    public static int OMEGA = -1;

    private final int[] marking;

    public Marking(int... marking) {
        this.marking = new int[marking.length];
        System.arraycopy(marking, 0, this.marking, 0, marking.length);
    }

    public int getMarking(int place) {
        return marking[place];
    }

    @Override
    public boolean equals(Object anotherMarking) {
        if (!(anotherMarking instanceof Marking)) {
            return false;
        }
        Marking that = (Marking) anotherMarking;
        for (int i = 0; i < this.marking.length; i++) {
            if (that.marking[i] != this.marking[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = this.marking[0];
        for (int i = 1; i < this.marking.length; i++) {
            result = 31 * result + this.marking[i];// * i;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder strB = new StringBuilder("(");
        for (int pMarking : this.marking) {
            if (pMarking == OMEGA) {
                strB.append("\u03C9");
            } else {
                strB.append(pMarking);
            }
            strB.append(",");
        }
        return strB.deleteCharAt(strB.length() - 1).append(")").toString();
    }
}
