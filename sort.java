public class Application {

    public static void qSort(ArratList<Application<String>> bids, int head, int tail) {
        if (head >= tail || bids == null || bids.length <= 1) {
            return;
        }
        int i = head, j = tail, pivot = bids[(head + tail) / 2];
        while (i <= j) {
            while (UserController.getUser(bids[i].get(0)).getRating() < UserController.getUser(pivot.get(0)).getRating()) {
                ++i;
            }
            while (UserController.getUser(bids[j].get(0)).getRating() > UserController.getUser(pivot.get(0)).getRating()) {
                --j;
            }
            if (i < j) {
                ArratList<String> t = bids.get(i);
                bids.set(i, bids.get(j));
                bids.set(j, t);
                ++i;
                --j;
            } else if (i == j) {
                ++i;
            }
        }
        qSort(bids, head, j);
        qSort(bids, i, tail);
    }

    public static void main() {
        ArratList<ArratList<String> bids = task.getBidList();
        qSort(bids, 0, bids.length - 1)
        
    }
}