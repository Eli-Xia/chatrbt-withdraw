var Option = {
  props: ["label", "value"],
  template: '<li class="k-option" @click="selectOption(value,label,$event)"\
                <span>{{label}}</span>\
            </li>',
  data: function() {
      return {
        selected: false
      }
  },
  methods: {
    selectOption: function(value,label,$event) {
      this.$dispatch('input', value, label);
    }
  },
}