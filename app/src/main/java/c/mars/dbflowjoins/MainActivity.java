package c.mars.dbflowjoins;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.SelectListTransaction;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;
import java.util.Random;

import c.mars.dbflowjoins.tables.Box;
import c.mars.dbflowjoins.tables.Box_Table;
import c.mars.dbflowjoins.tables.Item_Table;
import c.mars.dbflowjoins.tables.Item;
import c.mars.dbflowjoins.tables.Rocket;
import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        FlowManager.init(this);

        reset();
        create();
        print();
    }

    void reset() {
        Delete.tables(Item.class, Box.class, Rocket.class);
    }

    int n = 0;
    Action1<Rocket> loadVanAction = van -> {
        Observable<Box> boxObservable = Observable.just("A", "B", "C", "D").map(s -> new Box(s + van.getName().charAt(0))).cache();
        boxObservable.forEach(box -> {
                    box.loadToVan(van);
                    box.save();
                }
        );
        Random random=new Random();

        boxObservable
                .forEach(box -> {
                    if(n%2==0) {
                        Item item = new Item(String.valueOf(n));
                        item.save();
                        box.setItem(item);
                        box.save();
                    }
                    ++n;
        });
    };

    void create() {
        Observable.just("SpaceShipOne", "Vostok").map(Rocket::new).forEach(van -> {
            van.save();
            loadVanAction.call(van);
        });
    }

    void print() {
        List<Rocket> allRockets = SQLite.select().from(Rocket.class).queryList();
        List<Box> allBoxes = SQLite.select().from(Box.class).queryList();

        Rocket rocket = allRockets.get(0);
        List<Box> rocketBoxes = rocket.getBoxes();

        List<Box> boxes= new Select()
                .from(Box.class)
                .where(Box_Table.name.like("A%"))
                .queryList();

        if(boxes.size()>0 && boxes.get(0).getItem()!=null) {
            Condition.In in = Item_Table.id.in(boxes.get(0).getItem().getId());
            if(boxes.size()>1){
                for(int i=1; i<boxes.size(); i++){
                    Box box=boxes.get(i);
                    if(box.getItem()!=null){
                        in.and(box.getItem().getId());
                    }
                }
            }

            List<Item> items = new Select()
                    .from(Item.class)
                    .where(in)
                    .queryList();

            displayItems(items);
        }

//        Join is better solution, but needs aliases for fields that are the same in both tables (id, name):
//        https://github.com/Raizlabs/DBFlow/issues/67
//        NameAlias I = new NameAlias("i");
//        NameAlias B = new NameAlias("b");
//        String idAlias = "bi", nameAlias="bn";
//        List<Item> items = new Select()
//                .from(Item.class).as("i")
//                .join(Box.class, Join.JoinType.INNER).as("b")
//                .on(
//                        Item_Table.id.withTable(I)
//                        .eq(Box_Table.item_id.withTable(B))
//                )
//                .where(Box_Table.name.withTable(B).like("A%"))
//                .and(Box_Table.id.withTable(B).greaterThan(0))
//                .queryList();
//        displayItems(items);

    }

    void displayItems(List<Item> items){
        Observable.from(items).forEach(item -> {
            Box box=new Select().from(Box.class).where(Box_Table.item_id.eq(item.getId())).querySingle();
            Timber.d(item.getName() + (box != null ? " ["+box.getName()+"]" : ""));
        });
    }

}
