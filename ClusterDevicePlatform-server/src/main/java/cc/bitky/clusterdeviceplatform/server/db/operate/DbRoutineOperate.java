package cc.bitky.clusterdeviceplatform.server.db.operate;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import cc.bitky.clusterdeviceplatform.messageutils.msg.statusreply.MsgReplyDeviceStatus;
import cc.bitky.clusterdeviceplatform.server.db.bean.routineinfo.HistoryInfo;
import cc.bitky.clusterdeviceplatform.server.db.bean.routineinfo.LampStatusHistory;
import cc.bitky.clusterdeviceplatform.server.db.repository.RoutineTableRepository;

@Repository
public class DbRoutineOperate {
    private final RoutineTableRepository repository;
    private final MongoOperations operations;

    @Autowired
    public DbRoutineOperate(RoutineTableRepository repository, MongoOperations operations) {
        this.repository = repository;
        this.operations = operations;
    }

    /**
     * 更新员工的考勤表
     *
     * @param employeeObjectId 员工的 ObjectId，该值不能为 Null
     * @param chargeStatus     设备状态包
     */
    public void updateRoutineById(String employeeObjectId, MsgReplyDeviceStatus chargeStatus, MsgReplyDeviceStatus.Type type) {

        Query query = new Query(Criteria.where("_id").is(new ObjectId(employeeObjectId)));

        HistoryInfo historyInfo = new HistoryInfo(chargeStatus.getTime(), chargeStatus.getStatus());
        Update update;

        switch (type) {
            case WORK:
                update = new Update().push("WorkStatus", historyInfo);
                break;
            case CHARGE:
                update = new Update().push("ChargeStatus", historyInfo);
                break;
            default:
                return;
        }
        operations.upsert(query, update, LampStatusHistory.class);
//        Optional<LampStatusHistory> optional = repository.findById(employeeObjectId);
//        LampStatusHistory document = optional.orElseGet(() -> {
//            LampStatusHistory temp = new LampStatusHistory();
//            temp.setId(employeeObjectId);
//            return temp;
//        });
//        switch (type) {
//            case WORK:
//                document.getWorkStatus().add(new HistoryInfo(chargeStatus.getTime(), chargeStatus.getStatus()));
//                break;
//            case CHARGE:
//                document.getChargeStatus().add(new HistoryInfo(chargeStatus.getTime(), chargeStatus.getStatus()));
//                break;
//            default:
//        }
//        repository.save(document);
    }
}
