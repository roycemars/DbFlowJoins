package c.mars.dbflowjoins.tables;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import c.mars.dbflowjoins.Db;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Constantine Mars on 12/21/15.
 *
 * rocket can contain multiple boxes
 */
@Data @NoArgsConstructor
@ModelContainer
@Table(database = Db.class)
public class Rocket extends BaseModel {
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    String name;

    public Rocket(String name){
        this.name=name;
    }

    List<Box> boxes;

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE})
    public List<Box> getBoxes() {
        if (boxes == null || boxes.isEmpty()) {
            boxes = SQLite.select()
                    .from(Box.class)
                    .where(Box_Table.rocket_id.eq(id))
                    .queryList();
        }
        return boxes;
    }
}
