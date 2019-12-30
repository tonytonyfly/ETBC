package club.crabglory.www.factory.presenter.account;

import java.util.List;

import club.crabglory.www.common.basic.presenter.SourcePresenter;
import club.crabglory.www.factory.contract.AccountDataSource;
import club.crabglory.www.data.model.db.User;
import club.crabglory.www.data.model.view.UserModelView;
import club.crabglory.www.data.model.persistence.Account;
import club.crabglory.www.factory.repository.MineRepository;
import club.crabglory.www.factory.R;
import club.crabglory.www.factory.contract.MineContract;

public class MinePresenter extends SourcePresenter<User, UserModelView, AccountDataSource, MineContract.View>
            implements MineContract.Presenter{


    public MinePresenter(MineContract.View view) {
        super(new MineRepository(), view);
    }


    @Override
    public void onDataLoaded(List<User> users) {
        if (users.size() == 0){
            mView.showError(R.string.error_not_login);
            // 会制造默认的User
            mView.loadUserInfo(Account.getUser());
        }else {
            mView.loadUserInfo(users.get(0));
        }
    }
}
