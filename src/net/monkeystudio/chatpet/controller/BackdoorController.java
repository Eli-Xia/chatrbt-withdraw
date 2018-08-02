
package net.monkeystudio.chatpet.controller;

import net.monkeystudio.base.utils.RespHelper;
import net.monkeystudio.chatrbtw.mapper.ChatPetMapper;
import net.monkeystudio.chatrbtw.service.ChatPetService;
import net.monkeystudio.chatrbtw.service.WxFanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author xiaxin
 */

@Controller
@RequestMapping(value = "/backdoor")
public class BackdoorController {

    @Autowired
    private RespHelper respHelper;

    @Autowired
    private ChatPetService chatPetService;
    @Autowired
    private WxFanService wxFanService;
    @Autowired
    private ChatPetMapper chatPetMapper;


    /**
     * @param request
     * @param response
     * @return
     */
//    @ResponseBody
//    @RequestMapping(value = "/revise-data", method = RequestMethod.GET)
//    public RespBase getChatPetInfo(HttpServletRequest request, HttpServletResponse response){
//
//        List<ChatPet> chatPets = chatPetMapper.selectAll();
//        for(ChatPet chatPet: chatPets){
//            if(chatPet.getWxPubOriginId() != null){
//                String wxPubOriginId = chatPet.getWxPubOriginId();
//                String wxFanOpenId = chatPet.getWxFanOpenId();
//
//                WxFan wxFan = wxFanService.getWxFan(wxPubOriginId, wxFanOpenId);
//                Integer wxFanId = wxFan.getId();
//                Integer chatPetId = chatPet.getId();
//                chatPetMapper.updateWxFanId(chatPetId,wxFanId);
//            }
//        }
//        return respHelper.ok();
//    }

}
