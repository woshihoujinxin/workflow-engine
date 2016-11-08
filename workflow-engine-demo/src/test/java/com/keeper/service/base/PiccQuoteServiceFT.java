package com.keeper.service.base;

import com.workflow.engine.core.service.entity.vo.SupplierVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houjinxin on 16/4/13.
 */
public abstract class PiccQuoteServiceFT extends AbstractQuoteServiceFT {

    @Override
    public List<SupplierVO> getSuppliers() {
        return new ArrayList<SupplierVO>() {{
            add(new SupplierVO("人保", "picc"));
        }};
    }

}
