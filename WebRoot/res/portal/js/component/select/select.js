var Select = {
  props: ["value", "placeholder", "selected"],
  template: '<div class="k-select blur-input" :class="{ isfocus: isFocus }" @click="onHover($event)">\
  <input type="text" v-model="label" readonly="readonly" :placeholder="placeholder" />\
  <ul :class="{ active: isFocus }"><slot></slot></ul>\
  </div>',
  data: function() {
      return {
        label: '',
        isFocus: false,
      }
  },
  created: function() {
    this.label = this.selected;
  },
  methods: {
    onHover: function($event) {
      this.isFocus = !this.isFocus;
    },
  },
  events: {
    'input': function(value, label) {
      this.label = label;
      this.$emit('change', this.value, value);
    }
  }
}