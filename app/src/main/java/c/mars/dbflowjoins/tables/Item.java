package c.mars.dbflowjoins.tables;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import c.mars.dbflowjoins.Db;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Constantine Mars on 12/21/15.
 */
@Data @NoArgsConstructor
@Table(database = Db.class)
public class Item extends BaseModel
{
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    String name;

    public Item(String name){
        this.name = name;
    }
}
