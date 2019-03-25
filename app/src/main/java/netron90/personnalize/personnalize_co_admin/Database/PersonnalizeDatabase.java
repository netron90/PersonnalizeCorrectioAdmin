package netron90.personnalize.personnalize_co_admin.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by CHRISTIAN on 17/02/2019.
 */

@Database(entities = {DiapositiveFormat.class, DiapoImagePath.class, DocumentAvailable.class}, version = 1)
public abstract class PersonnalizeDatabase extends RoomDatabase {

    public abstract UserDao userDao();
}