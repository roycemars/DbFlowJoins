package c.mars.dbflowjoins.tables;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

import c.mars.dbflowjoins.Db;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by Constantine Mars on 12/21/15.
 * box can contain only one item
 */
@EqualsAndHashCode(callSuper = false)
@Data @NoArgsConstructor
@ModelContainer
@Table(database = Db.class)
public class Box extends BaseModel {
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    String name;

    public Box(String name){
        this.name=name;
    }

    @ForeignKey(saveForeignKeyModel = false)
    ForeignKeyContainer<Rocket> rocket;

    public void loadToVan(Rocket rocket){
        this.rocket = FlowManager.getContainerAdapter(Rocket.class).toForeignKeyContainer(rocket);
    }

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    Item item;
}
