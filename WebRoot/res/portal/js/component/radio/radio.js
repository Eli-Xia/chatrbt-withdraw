var Radio = {
    model: {
        prop: 'checked',
        event: 'change'
    },
    props: ["checked", "value"],
    template: '<label class="radio-wrap" :class="{ active: checked==value}"><span><span class="radio-btn"></span>\
    <input type="radio" :checked="checked==value" :value="value"></span>\
    <slot></slot>\
    </label>',
    data: function() {
        return {}
    }
}