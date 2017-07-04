package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.CommonUtils;
import cn.snzo.common.Constants;
import cn.snzo.common.ObjectResult;
import cn.snzo.service.IRecordingService;
import cn.snzo.service.ImageService;
import cn.snzo.vo.RecordingShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@RestController
@RequestMapping("/api")
public class RecordingController extends BaseController{

    @Autowired
    private IRecordingService recordingService;

    @Autowired
    private ImageService imageService;


    /**
     * 下载录音
     * @param filename
     * @param response
     * @return
     */
    @RequestMapping(value = "/recording/download" ,method = RequestMethod.GET)
    public ObjectResult download(@RequestParam(value = "filename",required = true)String filename,
                                    @RequestParam(value = "filepath",required = true)String filepath,
                                    HttpServletResponse response) {

        if(filename.contains("../") || filepath.contains("../")){
            return failureRes("文件名包含非法字符");
        }
        try{
            boolean success = imageService.downloadImage(filename, response.getOutputStream(), filepath);
            if(success){
                response.flushBuffer();
                response.setContentType("application/octet-stream");
                return successRes("下载成功");
            }
        }catch (Exception e){
            return failureRes("下载异常");
        }
        return failureRes("获取失败");
    }


    /**
     * 删除录音
     * @param rid
     * @return
     */
    @RequestMapping(value = "/recording/{rid}", method = RequestMethod.DELETE)
    public ObjectResult delete(@PathVariable("rid")int rid){
        int ret = recordingService.delete(rid);

        if (ret == 1) {
            return successRes("删除成功");
        } else {
            return failureRes("删除失败");
        }
    }

    /**
     * 查询录音
     * @param
     * @return
     */
    @RequestMapping(value = "/recording/page", method = RequestMethod.GET)
    public ObjectResult findPage(@RequestParam(name = "filename", required = false)String filename,
                                 @DateTimeFormat(pattern = Constants.FORMATE_yyyyMMddHHmmss)
                                 @RequestParam(name = "createStart", required = false) Date createStart,
                                 @DateTimeFormat(pattern = Constants.FORMATE_yyyyMMddHHmmss)
                                 @RequestParam(name = "createEnd", required = false) Date createEnd,
                                 @RequestParam(name = "currentPage", required = false)Integer currentPage,
                                 @RequestParam(name = "pageSize", required = false)Integer pageSize,
                                 HttpServletResponse response){
        Page<RecordingShow> page = recordingService.getPage( filename, createStart, createEnd,currentPage, pageSize);
        CommonUtils.setResponseHeaders(page.getTotalElements(), page.getTotalPages(), page.getNumber(), response);
        System.out.println(page.getContent());
        return successRes(page.getContent());
    }
}
