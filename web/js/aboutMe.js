$.fn.tagcloud.defaults = {
  size: {start: 14, end: 20, unit: 'pt'},
  color: {start: '#cde', end: '#282'}
};

$(document).ready(function () {
  $('#SkillCloud a').tagcloud();
});